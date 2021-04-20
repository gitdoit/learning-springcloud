package org.seefly.microserviceconsumer.controller;

import org.seefly.microservice.provider.api.service.SleepFeignApi;
import org.seefly.microservice.provider.api.service.fallback.SleepFeignApiFallback;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演示在客户端开启断路器
 *  配置类上： @EnableHystrix
 *  引入Feign客户端，让spring扫描到，放入容器
 *  容器注入Fallback实例
 *
 * 关于超时
 *   ribbon:它自己有一个超时限制，默认1秒，可以通过配置改掉
 *   hystrix:它也有一个超时限制，默认1秒
 *   feign:它也有一个超时限制
 * 这三者之间的调用包含关系，我现在还没摸清楚，但是通过现象可以知道的是
 *   1、如果feign配置了超时3秒，hystrix配置了超时1秒，如果调用耗时2.x秒，feign是没有问题，但是会在hystrix报错，执行fallback
 *
 * 关于fallback
 *  Feign中的fallback调用依赖于Hystrix，并提供给客户端使用
 *  但是什么情况下触发fallback
 *
 * 关于线程池
 *  Feign配合Hystrix的话，一个微服务使用公用一个线程池，线程池名字取决于 @FeignClient(name = "microservicecloud-dept这个"
 *  如果只是普通方法配合@HystrixCommand 的话，当前类中所有Hystrix标注的方法公用一个
 *  可以使用@HystrixCommand( threadPoolKey="6666", 来决定使用那个线程池
 *
 *
 * @author liujianxin
 * @date 2021/4/14 16:44
 */
 @Import(SleepFeignApiFallback.class)
@RestController
@RequestMapping("/time-out")
public class HystrixController {
    private final SleepFeignApi sleepFeignApi;
    
    public HystrixController(SleepFeignApi sleepFeignApi) {
        this.sleepFeignApi = sleepFeignApi;
    }
    
    /**
     * sleepFeignApi.sleep根据传入的ms时长进行休眠，传入负数报异常
     *
     * 通过调用这个接口测试消费方控制的服务熔断、降级策略
     * 详见配置文件
     */
    @GetMapping("sleep/{duration}")
    public String sleep(@PathVariable("duration") Long duration){
        sleepFeignApi.sleepForSelf(duration);
        return sleepFeignApi.sleep(duration);
    }

}
