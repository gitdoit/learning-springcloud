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

```java
boolean renew() {
    EurekaHttpResponse<InstanceInfo> httpResponse;
    try {
        httpResponse = eurekaTransport.registrationClient.sendHeartBeat(instanceInfo.getAppName(), instanceInfo.getId(), instanceInfo, null);
        if (httpResponse.getStatusCode() == 404) {
            REREGISTER_COUNTER.increment();
            // 这就比较有意思了，404说明你服务还没注册，去注册去吧
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

