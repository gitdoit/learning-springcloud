# 使用Feign

## 一、简介

`Feign`的中文直译叫做佯装，是一个声明式的伪`Http`客户端，使远程接口调用像是在调用一个方法一样。使用 `Feign`只需要声明一个接口并注解就行了。默认集成了 `Ribbon` 负载均衡，并和 `Eureka` 结合实现了负载均衡的效果。

### 版本

说真的，微服务这一套各个组件版本都乱的要命。看着网上各种教程，依赖版本不一，导致里面的各种组件用法也不一样，都是坑。例如，我目前在项目中使用的`Spring Cloudd` 的 `Hoxton.SR3`版本

```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-dependencies</artifactId>
	<version>Hoxton.SR3</version>
	<type>pom</type>
	<scope>import</scope>
</dependency>
```

其中就引入了对`Feign`的版本控制

```xml
 <dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-openfeign-dependencies</artifactId>
	<version>2.2.2.RELEASE</version>
	<type>pom</type>
	<scope>import</scope>
</dependency>
```

所以在实际项目中使用的时候，只要添加这个依赖就行了

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

但是，往前推几个版本例如 `Dalston.SR1` 就不一样了，毕竟中间差了好几个大版本

所以需要引入，这个依赖

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
```

**所以，这俩东西有什么区别？**

1、[Difference between openfeign or feign](https://stackoverflow.com/questions/55942060/difference-between-openfeign-or-feign)

`Feign`是`OpenFeign`的前身，并对`SpringMVC`相关的注解做了支持

## 二、如何使用

### 1、前置步骤

1. 开启服务注册中心
2. 创建`Feign-api`模块，该模块只提供接口不提供实现。实现的服务和依赖的服务同时引入这个模块即可。

### 2、引入依赖

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

### 3、创建接口

例如，我要对一个部门进行增删改查，我需要在 `Feign-api`模块下创建接口如下

```java
// value指定调用微服务的名称，下面的接口对应服务提供者的接口
@FeignClient(value = "microservicecloud-dept")
public interface DeptFeignApi {

    @RequestMapping(value = "dept/add",method = RequestMethod.POST)
    boolean add(@RequestBody Dept dept);

    @RequestMapping(value = "dept/get/{id}",method = RequestMethod.GET)
    Dept get(@PathVariable("id") Long id);

    @RequestMapping(value = "dept/list",method = RequestMethod.GET)
    List<Dept> list();

}
```

### 4、实现模块引入依赖

由于上面只是定义了一个接口，需要在实现的地方引入这个模块，同时再对这个接口做实现；

当然，你也可以不用实现这个接口，实现接口的目的只是保证别人的引用和你的实现是一致的，减少犯错的可能性。所以如果不实现，那么就要保证你的实现类和上面声明的接口方法签名一致，`url`一致。

如下，我们也可以不用实现上述接口。

```java
@RestController
public class DeptControllerByFeign {
    private final DeptFeignApi deptFeignApi;

    public DeptControllerByFeign(DeptFeignApi deptFeignApi) {
        this.deptFeignApi = deptFeignApi;
    }
    @GetMapping("feign/all")
    public List<Dept> getAll(){
        return deptFeignApi.list();
    }

    @PostMapping("feign/add")
    public void add(@RequestBody Dept dept){
        deptFeignApi.add(dept);
    }

    @GetMapping("feign/dept/{id}")
    public Dept get(@PathVariable("id") Long id) {
        return deptFeignApi.get(id);
    }

}
```

### 5、消费者使用Feign客户端

1. 项目引入依赖，在消费者模块中引入你刚才写的`Feign-api`模块

1. 在消费者项目的启动类上添加注解

   `@EnableFeignClients(basePackages = "top.seefly.microservice.provider.api.service")`

   这个注解表示，扫秒指定包下的Feign客户端，生成代理Feign客户端，好让你在容器中拿到并使用。

2. 注入 `Feign`客户端,并使用

```java
@RestController
public class DeptControllerByFeign {
    private final DeptFeignApi deptFeignApi;

    public DeptControllerByFeign(DeptFeignApi deptFeignApi) {
        this.deptFeignApi = deptFeignApi;
    }
    @GetMapping("feign/all")
    public List<Dept> getAll(){
        return deptFeignApi.list();
    }

    @PostMapping("feign/add")
    public void add(@RequestBody Dept dept){
        deptFeignApi.add(dept);
    }

    @GetMapping("feign/dept/{id}")
    public Dept get(@PathVariable("id") Long id) {
        return deptFeignApi.get(id);
    }

}
```

## 总结

可以看出来，`Feign`的使用还是非常简单的，应对一些普通场景都不需要做什么调整，需要做定制化那么他也提供了很多配置。后面再说