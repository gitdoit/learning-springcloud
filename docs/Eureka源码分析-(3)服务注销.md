# Eureka源码分析-(3)服务注销/下线

## 一、服务端发起注销

### 1、触发注销

>  和服务主动注册一样，利用`Spring`生命周期的`stop`阶段，触发一次注销动作

```java
@Override
public void EurekaAutoServiceRegistration#stop() {
  // 调用下面这个方法
  this.serviceRegistry.deregister(this.registration);
  this.running.set(false);
}

@Override
public void EurekaServiceRegistry#deregister(EurekaRegistration reg) {
  if (reg.getApplicationInfoManager().getInfo() != null) {
    // 我们知道，利用ApplicationInfoManager设置实例状态，会导致一次注册动作
    reg.getApplicationInfoManager().setInstanceStatus(InstanceInfo.InstanceStatus.DOWN);
    // 调用注销方法
    reg.getEurekaClient().shutdown();
  }
}

```

### 2、发起注销

> 主要是释放一些资源，例如线程池。同时发起注销请求到注册中心
>
> 下面的 `unregister()`方法就是向 `apps/appName/id`地址发起一个`delete`请求，就不看了

![carbon (17)](http://qiniu.seefly.top/carbon%20(17).png)



## Eureka-Server 接收下线

### 1、`PeerAwareInstanceRegistryImpl#cancel()`

> 处理逻辑逻辑比较简单，先调用父类下线，再变更参数阈值

```java
@Override
public boolean cancel(final String appName, final String id,
                      final boolean isReplication) {
  if (super.cancel(appName, id, isReplication)) {
    // 集群同步
    replicateToPeers(Action.Cancel, appName, id, null, null, isReplication);
    synchronized (lock) {
      // 变更服务剔除需要用到的两个参数阈值
      if (this.expectedNumberOfRenewsPerMin > 0) {
        // 期望每分钟收到的心跳数-2
        this.expectedNumberOfRenewsPerMin = this.expectedNumberOfRenewsPerMin - 2;
        // 每分钟最少需要收到的心跳数
        this.numberOfRenewsPerMinThreshold =
          (int) (this.expectedNumberOfRenewsPerMin * serverConfig.getRenewalPercentThreshold());
      }
    }
    return true;
  }
  return false;
}
```

### 2、`AbstractInstanceRegistry#internalCancel`

> 从注册表中移除，纪录变更等

![carbon (18)](http://qiniu.seefly.top/carbon%20(18).png)



### 总结

在客户端利用`spring`的生命周期，来触发注销动作，同时销毁线程池等。但是比较奇怪的是，由于先利用`ApplicationInfoManger`把实例状态设置为`DOWN`会触发注册动作，而后又发出注销请求。不知道这样做的原因是什么



在服务端主要做了两件事

一是把实例从注册表中移除

二是变更服务剔除需要的两个参数的阈值。