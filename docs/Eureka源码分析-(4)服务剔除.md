# Eureka源码分析-(4)服务剔除/过期

## 一、自我保护机制

> 在说服务剔除之前了解一下`Eureka`的自我保护机制；
>
> 简单点来说，在某段时间内`Eureka Server`丢失了过多的客户端(就是收到的心跳数少了很多)，这个时候`Eureka Server`就认为自己的网络出现了问题发生了网络分区，收到到心跳，而不是客户端们挂掉了。这时就会进入自我保护模式，不再剔除过期的服务。这样就可以防止误删除。**自我保护模式是一种对网络异常的安全保护措施，使用它可以让Eureka集群更健壮、稳定**

### 1、自我保护机制的实现

在`Eureka`中利用两个参数阈值来控制自我保护机制的开关

1. `expectedNumberOfRenewsPerMin` 期望每分钟收到的心跳次数

   计算公式为: `expectedNumberOfRenewsPerMin`  = 当前实例数量 * 2

   > 为什么乘2？因为默认情况下，在客户端中30秒/次发送心跳。所以这里被硬编码了
   >
   > 这一点可以在之前服务注册文章中看到。

2. `numberOfRenewsPerMinThreshold` 每分钟最少应该收到的心跳次数

   计算公式为：`numberOfRenewsPerMinThreshold`   = `expectedNumberOfRenewsPerMin`  * 续租百分比

   其中续租百分比可在配置文件中配置 `eureka.renewalPercentThreshold`默认为0.85

### 2、自我保护机制的触发条件

1. 开启自我保护功能 `eureka.enableSelfPreservation=true`，默认是开启的。
2. 同时每分钟心跳次数 < `numberOfRenewsPerMinThreshold` 

### 3、何时更新两个参数阈值？

1. **服务注册时**

   可在`com.netflix.eureka.registry.AbstractInstanceRegistry#register`中看到

   逻辑是：

   ​	`expectedNumberOfRenewsPerMin += 2`

      `numberOfRenewsPerMinThreshold`  = `expectedNumberOfRenewsPerMin  * 0.85`

2. **服务注销时**

   可在`PeerAwareInstanceRegistryImpl#cancel()`中看到

   逻辑是

   ​	`expectedNumberOfRenewsPerMin -= 2`

   ​    `numberOfRenewsPerMinThreshold`  = `expectedNumberOfRenewsPerMin  * 0.85`

3. **定时重置时**

   > `Eureka Server`启动之后，开启定时调度，15分钟/次计算这俩阈值；
   >
   > 就是算法我看不明白？？？
   >
   > 1.关掉自我保护，每次都重新计算
   >
   > 2.如果自我保护打开，那么当前实例数*2 > 0.85 * 每分钟最小心跳数时才计算。
   >
   > 为什么是这样？？？？？

   ![scheduleRenewalThresholdUpdateTask](http://qiniu.seefly.top/scheduleRenewalThresholdUpdateTask.png)

4. `Eureka Server`初始化时

   > 从集群内其他节点拉过来的实例数量根据计算公式算这俩东西



## 二、服务剔除

> 服务剔除的目的就是踢掉那些由于种种原因没有正常注销的服务。
>
> 服务剔除还要配合自我保护机制，防止由于`Eureka Server`自身的网络原因，剔除了正常的服务。

### 1. 使用一个定时调度来定时清理吧

> `Eureka Server`初始化的时候开启一个定时调度，按照默认60秒/次的频率定时检查注册表中过期的实例。
>
> 同时由于定时调度由于种种原因并不会十分准确的按照规定的时间点来执行，这时候`Eureka Server`则会计算两次任务实际执行的间隔来获得一个补偿时间(也就是误差值，我让你12点准时放个屁，你到点之后憋了2秒才放，这2秒就是误差)

![task](http://qiniu.seefly.top/task.png)



### 2. 执行清理

> 逻辑都在 `com.netflix.eureka.registry.AbstractInstanceRegistry#evict(long)`中
>
> 整个方法我分解为三个部分说，这样中间能够贴出来其他的逻辑而不影响阅读

#### 1、能够执行剔除操作

> 如果开启了自我保护，并且上一分钟心跳数 < 最少心跳阈值，就说明当前处于自我保护模式，不能剔除任何实例。

![carbon (19)](http://qiniu.seefly.top/carbon%20(19).png)



#### 2、获取所有的过期租约

> 遍历注册表中所有注册信息，将过期的租约拿出来

![carbon (20)](http://qiniu.seefly.top/carbon%20(20).png)

#### 3、 分批剔除

>为什么要分批剔除？
>
>即使关闭了自我保护模式，`eureka server`也不会在一个周期内将过期的实例全部剔除。可能是为了保证可用性？

![carbon (21)](http://qiniu.seefly.top/carbon%20(21).png)

#### 4、移除实例信息

> 在主动注销里面可以看到，最后调用的是同一个方法
>
> `AbstractInstanceRegistry#internalCancel`就不再赘述了