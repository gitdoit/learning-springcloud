# Eureka源码分析-(7)覆盖状态

> 用途就是为了方便管理员在不关闭实例的情况下给实例设置一个标志位，让它处于一个暂时下线的状态。这种需求很常见，例如开发环境，服务器上有一个实例A，我本地需要调试，想要所有请求都打到本地。就调用接口让实例A暂时下线，而不是真正的kill掉这个实例。等调试完了之后再让它上线即可。

### 一、使用方式

- 更新实例状态

  put请求：

  `http://localhost:7001/eureka/apps/{appName}/{appId}/status?value={status}`

### 二、原理

不想展开了，在`AbstractInstanceRegistry`中，使用`ConcurrentMap<String, InstanceStatus> overriddenInstanceStatusMap`来保存`instanceId:overriddenStatus`，就是实例id对应的覆盖状态。如果你手动给一个实例设置了覆盖状态，那么在这里就会有记录。就算这个实例下次重新带着`UP`状态来注册，也会从这个`MAP`中去它的覆盖状态，防止这种情况  “哎？？我刚才让A下线了啊？怎么又上线了？”

### 三、我比较疑惑的地方

> 为什么一个服务状态需要两个属性来表示？如果主动调用`API`设置状态的话直接更改注册表中实例的状态不就行了？
>
> 会不会是这种原因？
>
> 主动调用`API`下线实例A，设置状态到`OUT_OF_SERVICE`
>
> 这时由于某种原因导致实例A重新注册了，那么此时注册中心对于实例A的状态就会更新为`UP`。
>
> 这样就会导致之前主动设置的`OUT_OF_SERVICE`就失效了。。应该是这个原因
>
> 详见`github`讨论：[移除覆盖状态](https://github.com/Netflix/eureka/issues/389)

```java
public class InstanceInfo{
  // 实例真正的状态
  private volatile InstanceStatus status = InstanceStatus.UP;
  // 覆盖状态
  private volatile InstanceStatus overriddenstatus = InstanceStatus.UNKNOWN;
}
```

