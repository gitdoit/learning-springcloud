# Eureka源码分析-(3)服务注销和剔除

## 四、服务注销`Cancel`

> 服务注销的逻辑比较简单，触发逻辑是步骤 一.2.(1)下的实现了`SmartLifecycle`接口的`EurekaAutoServiceRegistration`。在`Spring`的生命周期里的`stop`阶段，调用`EurekaServiceRegistry#deregister()` -> `DiscoveryClient#shutdown`。同时我发现一个比较奇怪的事情，在`deregister()`方法里同时还会调用`applicationInfoManager.setInstanceStatus(InstanceStatus.DOWN);`,
>
> 看过上面的代码应该知道，这一步最终会触发`discoveryClient.register()`完成一次注册动作；这都下线了为什么还更新一下注册信息？虽然`status=DOWN`

## 五、过期剔除

> 正常情况下，服务下线都会向注册中心发送一个注销的请求。但是异常情况下，这个请求可能不会被发出。所以需要一个机制，来应道这种异常情况。
