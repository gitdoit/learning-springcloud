# Eureka源码分析-(2)服务发现

## 一、服务发现

1. 在`DiscoveryClient`构造函数中会调用`initScheduledTasks`方法
2. 判断配置文件是否开启了`eureka.client.fetch-registry=true`，如果开启则继续
3. 获取配置文件中的定时拉取`registry`的时间间隔: `eureka.client.registry-fetch-interval-seconds`,默认为30秒。
4. 开启一个`ScheduledExecutorService`定时调度，定时拉取注册中心的服务注册信息



## 二、