# Eureka源码分析-(1)

## 一、程序启动首次注册

> 整合了配合`SpringCloud`用起来非常方便，因为有`Spring`的自动化配置。

### 1、`spring.factories`

> 比较重要的是`EurekaClientAutoConfiguration`自动配置类

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.cloud.netflix.eureka.config.EurekaClientConfigServerAutoConfiguration,\
org.springframework.cloud.netflix.eureka.config.EurekaDiscoveryClientConfigServiceAutoConfiguration,\
org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration,\
org.springframework.cloud.netflix.ribbon.eureka.RibbonEurekaAutoConfiguration
```

### 2、 `EurekaClientAutoConfiguration`

> `SpringBoot`整合`Eureka`的自动配置类，帮我们自动配置了一些基本的组件。

#### （1） `EurekaAutoServiceRegistration`

> `EurekaAutoServiceRegistration`这个东西的任务也很简单，就是为了在服务启动的时候向注册中心注册当前实例，就不展开了。它实现了`SmartLifecycle`接口，所以在`IOC`容器初始化完成之后会回调它的`start()`方法。而在这个`start()`方法中则触发了一次向注册中心注册当前实例的动作。

<img src="http://qiniu.seefly.top/carbon%20(1)_1599041269760.png" ></img>

##  二、定时刷新注册信息

### 1.初始化-定时调度

> 比较核心的是在`DiscoveryClient`构造方法的第412行，初始化了一些定时调度器。用来管理心跳、注册、拉取注册信息等任务；
>
> 下面的代码省略了其他逻辑，只看和注册相关的

![carbon (10)](http://qiniu.seefly.top/carbon%20(10).png)

###  **2.实例信息复制器**:`InstanceInfoReplicator`

> 这个东西的任务就是主动刷新实例信息，如果实例信息被更新了就触发一次注册动作。
>
> 它的启动入口是上图中的 `instanceInfoReplicator.start()`方法

```java
// initialDelayMs就是首次注册在程序启动之后多少秒后执行，默认40秒
public void start(int initialDelayMs) {
  if (started.compareAndSet(false, true)) {
    // 设置当前实例为过期的，目的就是在一开始能够向注册中心注册
    instanceInfo.setIsDirty(); 
    // 这个比较有意思，没有使用定时调度，而是手动给了只执行一次的延时任务
    // 这需要看它的run()方法
    Future next = scheduler.schedule(this, initialDelayMs, TimeUnit.SECONDS);
    // 保存当前的任务，为什么？为了能够主动取消
    scheduledPeriodicRef.set(next);
  }
}
```

> 上面的`scheduler.schedule(this, initialDelayMs, TimeUnit.SECONDS);`实际执行的就是下面的代码

<img src="http://qiniu.seefly.top/run.png" ></img>

### 3. `DiscoveryClient#refreshInstanceInfo()`

> 可以看到上面的定时任务主要就是定时刷新当前实例的信息

<img src="http://qiniu.seefly.top/carbon%20(2).png" ></img>

### 4. `ApplicationInfoManager#setInstanceStatus(status);`

> 在上面的`refreshInstanceInfo()`方法中，可以看到，如果当前实例的状态发生了改变，则会通知应用信息管理器，更新状态；

<img src="http://qiniu.seefly.top/carbon%20(4).png" ></img>

### 5. `InstanceInfoReplicator#onDemandUpdate()`

> 这个方法目的是为了在实例状态发生改变的时候能够立即注册到注册中心，该方法实际触发的逻辑为
>
> `InstanceInfoReplicator.run()`
>
> --> `DiscoveryClient#refreshInstanceInfo()`
>
>   --> `ApplicationInfoManager.setInstanceStatus()` 
>
> ​	    -->  `listener.notify()` 
>
> ​		    --> `InstanceInfoReplicator.onDemandUpdate()`

```java
public boolean onDemandUpdate() {
  // 频率控制
  if (rateLimiter.acquire(burstSize, allowedRatePerMinute)) {
    scheduler.submit(new Runnable() {
      @Override
      public void run() {
        // 拿到下一次需要执行的任务
        Future latestPeriodic = scheduledPeriodicRef.get();
        // 如果还没执行，就取消，因为实例状态发生了改变。需要立即更新到注册中心
        // 这也就是为什么在run方法中需要保存Future的原因
        if (latestPeriodic != null && !latestPeriodic.isDone()) {
          latestPeriodic.cancel(false);
        }
        // 立即开始重新执行
        InstanceInfoReplicator.this.run();
      }
    });
    return true;
  } else {
    return false;
  }
}
```

### 6. 发起注册 `DiscoveryClient#register()`

> 目前情况下上，一下几种情况会触发注册动作
>
> 1. 当前`hostname`发生了改变
> 2. 当前心跳频率、租约时长发生了改变
> 3. 当前实例的`status`发生了改变`

```java
// 代码很简单就是发起一个post请求，请求体是当前实例信息
// 大头还是在注册中心那里
boolean DiscoveryClient#register() throws Throwable {
  // 省略异常处理
  EurekaHttpResponse<Void> httpResponse eurekaTransport.registrationClient.register(instanceInfo);
  return httpResponse.getStatusCode() == 204;
}
```

## 三、 Eureka-Server 接收注册

> 服务端接收注册请求可以在`ApplicationResource#addInstance`看到，这个接口逻辑比较简单，前面是做了些参数判断。为了节省篇幅直接看实际调用的`PeerAwareInstanceRegistryImpl#register`

```java
public void PeerAwareInstanceRegistryImpl#register(final InstanceInfo info, final boolean isReplication) {
  // 租约时长给个默认90秒
  int leaseDuration = Lease.DEFAULT_DURATION_IN_SECS;
  // 如果客户端提供了租约时长，就用客户端的
  if (info.getLeaseInfo() != null && info.getLeaseInfo().getDurationInSecs() > 0) {
    leaseDuration = info.getLeaseInfo().getDurationInSecs();
  }
  // 调用父类
  super.register(info, leaseDuration, isReplication);
  // 同步其他server实例
  replicateToPeers(Action.Register, info.getAppName(), info.getId(), info, null, isReplication);
}
```

### 一、一些数据结构

1. 租约：`Lease<T>`

   ```java
   // 租约持有者，就是实例信息
   private T holder;
   // 剔除时间
   private long evictionTimestamp;
   // 注册时间
   private long registrationTimestamp;
   // 开始服务时间
   private long serviceUpTimestamp;
   // 最后更新时间
   private volatile long lastUpdateTimestamp;
   // 租约时长，超过这个时长没有心跳过来，就意味着实例需要被剔除了
   private long duration;
   ```

2. 注册表

   > 在Eureka-Server中是这样存储注册的应用实例的
   >
   > 首先一个模块为了横向扩容，可能会有多个实例用来做负载均衡。例如一个用户模块的`applicationName=micro-user`，那么在同时存在3个实例的话在注册中心可以这样表示。
   >
   > `applicationName` 1---->3 `applicationInstance`，而在`Eureka`中是使用
   >
   > ` ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>> registry`来表示的。
   >
   > 其中最外层的`key`是`applicationName`，内层`map`的`key`是`appId`（是一个`hostname:applicationName:port`的组合），值为租约，且租约持有实例信息。

### 二、注册逻辑

![carbon (11)](http://qiniu.seefly.top/carbon%20(11).png)

注册的逻辑就是这样，后面会通过心跳来维持服务状态，避免超时被剔除。



## 总结

**客户端**

1. 服务启动的时候利用`spring`的生命周期的`start()`阶段，发送一次注册请求
2. 在程序启动40秒后，利用线程池定时以30秒/次的频率循环刷新实例信息，在`hostname`、租约频率、租约过期时间变更的时候，重新注册；
3. 利用监听者模式，在服务状态发生改变的时候重新注册，并取消线程池中未执行的扫秒任务。

**服务端**

1. 使用` ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>>`的数据结构保存注册信息

2. 把上次注册时间当作版本号比较，防止提交的注册信息比服务端存储的还旧

3. 每一个新注册的实例都会改变控制自我保护的两个参数。

   一个是每分钟最少收到多少次心跳，另一个是期望每分钟收到多少次心跳。

