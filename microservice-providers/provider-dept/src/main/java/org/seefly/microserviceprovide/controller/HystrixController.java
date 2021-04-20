package org.seefly.microserviceprovide.controller;

import org.seefly.microserviceprovide.service.SleepService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liujianxin
 * @date 2021/4/14 16:00
 */
@RestController
@RequestMapping("sleep")
public class HystrixController {
    private SleepService service;
    
    public HystrixController(SleepService service) {
        this.service = service;
    }
    
    /**
     * 测试服务调用方的熔断策略,根据传入的参数 睡眠指定时间
     */
    @GetMapping("/for/{duration}")
    public String sleep(@PathVariable Integer duration) throws InterruptedException {
        Thread.sleep(duration);
        return "休眠了:"+duration;
    }
    
    /**
     * 测试服务提供方的熔断策略,当执行超时或者发生异常,执行本地降级策略
     */
    @GetMapping("/server-side/{duration}")
    public String sleepForSelf(@PathVariable Long duration){
        return service.sleepTimeout(duration);
    }
    
    
    
    
}
