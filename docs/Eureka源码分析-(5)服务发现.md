# Eureka源码分析-(5)服务发现

## 一、服务发现之全量获取

> 如果开启了拉取注册列表 `eureka.client.fetch-registry=true`，那么在实例启动的时候，会执行一次全量获取，从注册中心拉取全量的注册表到本地缓存，之后按照默认配置`eureka.client.registry-fetch-interval-seconds=30` 30秒/次更新一次本地缓存。

### 1、程序启动全量获取

> 在`DiscoveryClient`的构造函数中可以看到，首次全量获取的逻辑

```java
private final AtomicReference<Applications> localRegionApps = new AtomicReference<Applications>();
DiscoveryClient(ApplicationInfoManager applicationInfoManager, 
                EurekaClientConfig config, 
                AbstractDiscoveryClientOptionalArgs args,
                Provider<BackupRegistry> backupRegistryProvider) {
  // 本地缓存
 	localRegionApps.set(new Applications());
  //...
  // fetchRegistry()这里会触发一次全量获取
  if (clientConfig.shouldFetchRegistry() && !fetchRegistry(false)) {
    fetchRegistryFromBackup();
  }
  initScheduledTasks();
}
```

### 2、拉取远程注册表

![carbon (22)](http://qiniu.seefly.top/carbon%20(22).png)

### 3、发起全量请求

<img src="http://qiniu.seefly.top/carbon%20(23).png" alt="carbon (23)" style="zoom:80%;" />

## 二、`Eureka Server`处理全量请求

### 1、处理请求接口

> 在`com.netflix.eureka.resources.ApplicationsResource#getContainers`处理全量获取请求

<img src="http://qiniu.seefly.top/carbon%20(24).png" alt="carbon (24)" style="zoom:80%;" />

### 2、`Eureka Server`的缓存设计`ResponseCacheImpl` 

> 为了提升性能，`Eureka Server`使用了缓存将注册表信息放到缓存里面，在提升性能的同时必然也会降低数据的实时性。但是`Eureka`不是说自己是 `AP`吗？ 那这个`C`就不是优先考虑的东西了。

#### 1）`ResponseCacheImpl`的数据结构

- `ConcurrentMap<Key, Value> readOnlyCacheMap` 只读缓存，是一个并发`map`
- `LoadingCache<Key, Value> readWriteCacheMap` 读写缓存，使用的是谷歌的`guava`缓存
- `AbstractInstanceRegistry registry` 注册表

#### 2）**缓存读取策略**

##### i、首先会从只读缓存中获取数据，如果没有再读取读写缓存

<img src="http://qiniu.seefly.top/carbon%20(25).png" alt="carbon (25)"  />

##### ii、如果读写缓存还没有则会读取注册表

> 步骤1中的`readWriteCacheMap.get(k)`如果发现缓存中没有则会实际读取注册表
>
> 这一点需要看一下读写缓存的初始化了，这需要对`LoadingCache`有些了解;

![carbon (26)](http://qiniu.seefly.top/carbon%20(26).png)

这里面的`generatePayload(key)`没什么好看的，如果是全量获取就直接读`registry.getApplications()`

#### 3）**缓存过期策略**

- **主动过期**

  > 通过之前的分析可以知道，我们可以通过主动注销、定时剔除来移除失效的实例然后**主动**使缓存过期。
  >
  > 需要注意的是，**失效的是读写缓存**，至于只读缓存后面会通过定时调度来进行同步。所以这里会出现一段时间内的数据不一致
  >
  > 生产环境问题不大，开发环境实例经常上下线就比较蛋疼了。

  ![carbon (27)](http://qiniu.seefly.top/carbon%20(27).png)

- **被动过期**

  > 在**读写缓存**初始化的代码上可以看到，默认一个缓存在写入180秒后过期；当然，即使过期了也不是意味着客户端的下一次拉取立马就能知道这个服务下线了，还要等只读缓存和读写缓存的同步；

- **定时刷新只读缓存**

  > 定时同步读写缓存和只读缓存的信息，使读写缓存和只读缓存保持一致，默认30秒一次。

  ![carbon (29)](http://qiniu.seefly.top/carbon%20(29).png)

## 总结

- 客户端在启动的时候进行一次注册表的全量获取(当然需要把`fetch-registry`打开)
- 服务端使用双层缓存来提高性能，请求过来先读只读缓存，如果只读缓存没有再读读写缓存，如果读写缓存还没有才会访问本地注册表。同时，读写缓存的数据有时效性，默认180秒后过期。为了使两个缓存之间进行数据同步，使用了定时调度，默认30秒。

一个服务下线可能很久才会被其他客户端感知到，因为这两个缓存的存在。例如一个服务下线主动注销了。服务端会剔除注册表中的租约，同时使租约中持有的实例信息的剔除时间属性置为当前时间。但是这并不意味着其他客户端下次拉取信息就能发现，因为拉取注册表使用只读缓存中取的。只读缓存还需要30秒的周和读写缓存同步，读写缓存的数据还有180秒的生存时间。



第0s： 服务B下线，主动注销，服务端剔除注册表中租约信息，使租约中的实例信息的剔除时间参数置为当前时间

第10s：服务A拉取注册表，此时只读缓存还没和读写缓存同步，没有发现

第40s：服务A经过一个拉取周期(30s)，再读取只读缓存，虽然两个缓存同步了，但是读写缓存中数据的180秒还没过期

...

第180s: 读写缓存过期了，两个缓存也同步了。这时候服务A才会发现服务B下线了