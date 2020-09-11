# Eureka源码分析-(6)服务发现

## 总结

> 我觉得还是把总结放在前面比较好，复习的时候比较方便。

- 

## 一、服务发现之增量获取

> 如果开启了拉取注册列表 `eureka.client.fetch-registry=true`，那么在实例启动的时候，会执行一次全量获取，从注册中心拉取全量的注册表到本地缓存，之后按照默认配置`eureka.client.registry-fetch-interval-seconds=30` 30秒/次更新一次本地缓存。

### 1、初始化定时调度

> 在`DiscoveryClient#initScheduledTasks`的构造函数中可以看到，启动了一个定时调度，方式是和心跳续约一样的

<img src="http://qiniu.seefly.top/%E5%88%9D%E5%A7%8B%E5%8C%96%E8%B0%83%E5%BA%A6.png" alt="初始化调度" style="zoom:80%;" />

### 2、增量获取注册表

> 上一步的定时调度执行的任务是调用`DiscoveryClient#refreshRegistry()` --> `DiscoveryClient#fetchRegistry()`
>
> 而对于`fetchRegistry()`方法的说明在[上一篇]([http://www.seefly.top/archives/eureka%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90-5%E6%9C%8D%E5%8A%A1%E5%8F%91%E7%8E%B0md#2%E6%8B%89%E5%8F%96%E8%BF%9C%E7%A8%8B%E6%B3%A8%E5%86%8C%E8%A1%A8](http://www.seefly.top/archives/eureka源码分析-5服务发现md#2拉取远程注册表))文章中说过了，这里就不再赘述。我们直接看增量获取的

<img src="http://qiniu.seefly.top/carbon%20(30).png" alt="carbon (30)" style="zoom: 80%;" />

### 3、合并增量数据

> 对增量数据做合并，其中实例状态是从服务端传过来了；还记得之前的服务端的主动剔除、注销、注册操作吗？每个实例都会设置一下`ActionType`，这里作用就体现到了。
>
> 下面的代码片段删除了一些其他逻辑。

<img src="http://qiniu.seefly.top/carbon%20(34).png" alt="合并增量数据" style="zoom:80%;" />



## 二、`Eureka Server`处理增量请求

### 1、处理请求接口

> 在`com.netflix.eureka.resources.ApplicationsResource#getContainerDifferential`处理全量获取请求；
>
> 和接口层面的处理和全量获取几乎一样，主要区别是请求类型`ResponseCacheImpl.ALL_APPS_DELTA`

<img src="http://qiniu.seefly.top/carbon%20(35).png" alt="carbon (35)" style="zoom:80%;" />

### 2、获取最近变更的实例信息

> 读取缓存的操作前面[全量获取]([http://www.seefly.top/archives/eureka%E6%BA%90%E7%A0%81%E5%88%86%E6%9E%90-5%E6%9C%8D%E5%8A%A1%E5%8F%91%E7%8E%B0md#iii%E5%88%A4%E6%96%AD%E8%AF%B7%E6%B1%82%E7%B1%BB%E5%9E%8B-%E8%AF%BB%E5%8F%96%E6%B3%A8%E5%86%8C%E8%A1%A8](http://www.seefly.top/archives/eureka源码分析-5服务发现md#iii判断请求类型-读取注册表))已经说过了，我们直接进入增量获取的部分

<img src="http://qiniu.seefly.top/carbon%20(37).png" alt="读取最近变更" style="zoom:80%;" />

### 3、关于最近变更队列

> 可以看到增量获取的关键就是最近变更队列，通过之前的几章我们看到了，如果服务主动注销、被动剔除、注册操作都会向这个最近变更对列插入一个元素；

#### 1）队列元素类型

> 数据结构很简单，一个是租约，一个是添加时间。这个`lastUpdateTime`用来更新最近变更队列，剔除过期元素用的。

```java
private static final class RecentlyChangedItem {
  private long lastUpdateTime;
  private Lease<InstanceInfo> leaseInfo;

  public RecentlyChangedItem(Lease<InstanceInfo> lease) {
    this.leaseInfo = lease;
    lastUpdateTime = System.currentTimeMillis();
  }

  public long getLastUpdateTime() {
    return this.lastUpdateTime;
  }

  public Lease<InstanceInfo> getLeaseInfo() {
    return this.leaseInfo;
  }
}
```

#### 2）入队时机

- **服务注册时**

  >  可在`AbstractInstanceRegistry#register:264`看到

  ```java
  Lease<InstanceInfo> lease = new Lease<InstanceInfo>(registrant, leaseDuration);
  //...
  registrant.setActionType(ActionType.ADDED);
  recentlyChangedQueue.add(new RecentlyChangedItem(lease));
  ```

- **服务剔除时**

  > 可在`AbstractInstanceRegistry#internalCancel:325`看到

  ```java
  leaseToCancel = gMap.remove(id);
  //...
  InstanceInfo instanceInfo = leaseToCancel.getHolder();
  //...
  instanceInfo.setActionType(ActionType.DELETED);
  recentlyChangedQueue.add(new RecentlyChangedItem(leaseToCancel));
  ```

- **服务注销时**

  > 服务注销和服务剔除最后调用同样的方法，入队逻辑相同。

- **实例状态更新时**

  > 可在`AbstractInstanceRegistry#statusUpdate:502`处看到

  ```java
  lease = gMap.get(id)
  InstanceInfo info = lease.getHolder();
  info.setActionType(ActionType.MODIFIED);
  recentlyChangedQueue.add(new RecentlyChangedItem(lease));
  ```

#### 3) 出队时机

> 通过一个定时调度，按照默认30秒/次扫秒最近变更队列。如果发现元素入队时间超过了3分钟就会被剔除。

<img src="http://qiniu.seefly.top/%E6%B8%85%E7%90%86%E6%9C%80%E8%BF%91%E5%8F%98%E6%9B%B4%E9%98%9F%E5%88%97.png" alt="清理最近变更队列" style="zoom:80%;" />

### 4、关于一致性**hash**

> 可以看到，在增量获取之后客户端会校验服务端传来的`hash`是否和本地相等，如果不等则发起全量获取。
>
> 但是这个`hash`是不是有点过于简单了？只是拼接了几个服务在线几个服务下线...那么在增量获取期间，服务A下线，服务B上线。那么计算出来的`hash`会是一样的。当然默认情况下不会发生这种情况，因为最近变更队列元素生存周期是3分钟，而客户端会30秒拉一次。怎么着都会知道谁上线谁下线了。除非特殊情况把最近变更队列元素生存周期设置的比客户端拉取周期还短，才会发生这种情况。
>
> 另外详见[github上的讨论](https://github.com/Netflix/eureka/pull/130)

- 计算方法

  `appsHashCode = status_+count_`,如`UP_3_DOWN_1`

## 总结

