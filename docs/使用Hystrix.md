# 使用`Hystrix`

## 一、简介

> [Hystrix Wiki](https://github.com/Netflix/Hystrix/wiki)
>
> 在微服务架构中，根据业务拆分成一个个的服务，服务和服务之间通过`RPC`或者`REST`相互调用，在`Spring Cloud`中可以使用`RestTemplate + Ribbon`和`Feign`的方式来调用。为了保证高可用，单个服务通常会集群部署，同时由于网络原因会导致某些实例的调用出现问题，调用这个服务就会出现线程阻塞。此时如果又大量的请求进来，则线程资源会被大量消耗且不被释放，导致服务瘫痪。这样会造成故障传播，对整个系统造成影响。为了解决这个问题，出现了断路器这种解决方案。

## 二、如何使用

首先需要知道的是，熔断是相对于服务调用者而言的，而不是服务提供者。所以需要在调用的时候做熔断控制。那么我们就需要在 消费者 那里配置熔断处理。

**引入依赖**

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
```

1. 对 `Feign`方式的调用做熔断处理

   由于可以用`REST`和`Feign`两种方式做调用，所以熔断处理也是不一样的，先说`Feign`的方式

   - 开启熔断配置

     ```yaml
     feign:
       hystrix:
         enabled: true
     ```

   - 编写Feign客户端熔断处理类，并将之注入到消费者服务的`IOC`容器中

     ```java
     public class DeptFeignApiFallBack implements DeptFeignApi {
         @Override
         public boolean add(Dept dept) {
             System.out.println("can not add dept...");
             return false;
         }
         @Override
         public Dept get(Long id) {
             System.out.println("can not get dept by id,fallback");
             return new Dept();
         }
         @Override
         public List<Dept> list() {
             System.out.println("can not get dept list,fallback...");
             return new ArrayList<>();
         }
     }
     ```

   - 在`@FeignClinet`中声明

     `@FeignClient(name = "microservicecloud-dept",fallback = DeptFeignApiFallBack.class)`

   - 这个时候将服务提供者关掉，再调用这个接口的话，就会走熔断处理了。

2. 对`REST`方式的调用做熔断处理

   - 编写熔断处理方法

     例如我想对`/dept/list`这个接口做熔断处理，那么

     ```java
     		@HystrixCommand(fallbackMethod = "errorList")
         @GetMapping("consumer/dept/list")
         public List<Dept> list() {
             return restTemplate.getForObject(url + "/dept/list", List.class);
         }
         // 上述接口方法失败的时候会走这里
         public List<Dept> errorList() {
             System.out.println("fallback...list....");
             return new ArrayList<>();
         }
     ```

     