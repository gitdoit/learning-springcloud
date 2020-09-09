# Eureka源码分析-(2)心跳续约

## 一、服务端定时发送心跳

### 1.初始化

> 入口还是在`DiscoveryClient#initScheduledTasks`这里，逻辑还是比较简单的

> 默认情况下，程序启动之后会以30s/次的频率向注册中心发送心跳续约。超时时间为90s，也就是说允许三次心跳失败。如果超过90秒注册中心没有接收到心跳就会认为你这个服务死掉了。

![carbon (12)](http://qiniu.seefly.top/carbon%20(12).png)

### 2. 定时执行心跳-`TimedSupervisorTask`

> 需要注意的是，并不是直接使用一个定时调度的线程池去定时发送心跳，这样的话对于心跳超时的处理就比较麻烦了。`eureka`使用了一个叫做`TimedSupervisorTask`的东西，来管理心跳。用来在心跳超时的时候，下一次心跳的时间点再往后推迟一些。例如一开始是按照30秒/次的来的，但是发生了超时，那么`TimedSupervisorTask`就会给30秒*2，下一次心跳就在一分钟以后了。以此类推，直到达到上限(默认5分钟)，之后会按照这个上限频率来进行心跳。如果某次心跳成功了，就会恢复为30秒/次

![carbon (13)](http://qiniu.seefly.top/carbon%20(13).png)

### 3. 执行心跳

>  心跳续约的方法很简单，就是发起请求，根据响应结果决定是不是需要注册。
>
>  心跳上报的数据也很简单，可以在服务端的接收接口看到，只包含下面几个参数
>
>  - 服务状态-`status`
>  - 版本号-`lastDirtyTimestamp`
>  - 覆盖状态-`overriddenstatus`

```java
boolean renew() {
    EurekaHttpResponse<InstanceInfo> httpResponse;
    try {
        httpResponse = eurekaTransport.registrationClient.sendHeartBeat(instanceInfo.getAppName(), instanceInfo.getId(), instanceInfo, null);
      // 响应404，表名注册中心想要你先去注册，可能是版本号冲突或者是你根本还没注册
        if (httpResponse.getStatusCode() == 404) {
            REREGISTER_COUNTER.increment();
            return register();
        }
        // 成功
        return httpResponse.getStatusCode() == 200;
    } catch (Throwable e) {
        return false;
    }
}
```

## 二、注册中心处理心跳

### 一、注册中心接收续租请求

> 心跳处理接口在注册中心的 `com.netflix.eureka.resources.InstanceResource#renewLease`
>
> 源码如下

<img src="http://qiniu.seefly.top/carbon%20(14).png" alt="carbon (14)" style="zoom:80%;" />



### 二、校验版本号

> `validateDirtyTimestamp(Long lastDirtyTimestamp, boolean isReplication)`我把它理解为校验版本号，既然是心跳续约，会有信息同步。网络环境不可靠的情况下，使用版本号(`lastDirtyTimestamp`)来控制数据一致性，就可以解决这个问题。

<img src="http://qiniu.seefly.top/carbon%20(15).png" alt="carbon (15)" style="zoom:80%;" />

### 三、更新续约

> 这里的逻辑也比较简单
>
> - 获取本地租约信息，没有返回false，从而导致响应404
>
> - 进行一些覆盖状态的校验，后面会说
>
> - 续约，就是更新`lastUpdateTimestamp`=当前时间+续约间隔
>
>   其实是不应该加续约间隔（`duration`）这个参数的，官方是说是个小问题
>
>   也没有修，修了会影响其他地方。

<img src="http://qiniu.seefly.top/carbon%20(16).png" alt="carbon (16)"  />