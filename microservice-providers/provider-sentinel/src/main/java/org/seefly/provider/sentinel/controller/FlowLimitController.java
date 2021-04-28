package org.seefly.provider.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.seefly.provider.sentinel.handler.CustomBlockHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演示限流效果
 * 1、qps限流
 *  每秒钟允许多少qps
 * 2、线程数限流
 *  一个线程服务一个请求，如果线程池中只有一个线程，当这个线程在服务请求A时，请求B，过来了，就会被限流。
 *  如果请求处理非常快，1ms处理一个，那么当前qps理论上就能达到1000.
 *
 * 限流触发降级的处理方式
 *  限流触发后会走降级方法，使用blockHandler配置
 *  1、耦合业务代码的配置方式配置在当前类中
 *  2、抽出来放在单独的降级处理方式中
 *  3、TODO 统一配置
 *
 * @author liujianxin
 * @date 2021/4/20 10:41
 */
@Slf4j
@RestController
@RequestMapping("/limit")
public class FlowLimitController {
    
    // qps限流-直接
    @GetMapping("/qps")
    public String byQps(){
        return "qps pass!";
    }
    // 线程数限流-直接
    @GetMapping("/thread")
    public String helloWorld() throws InterruptedException {
        // 线程数限流，睡一会，不然看不到效果
        Thread.sleep(1000);
        return "thread pass!";
    }
    
    
    
    // 演示关联，当前资源的限流与否，取决于下面接口的访问频率是否超限
    @GetMapping("/part-a")
    public String influenceA(){
        return "资源A";
    }
    @GetMapping("/part-b")
    public String influenceB(){
        return "资源B";
    }
    
    
    /**
     * 冷启动的时候进行预热
     * 某些接口平时无人访问，某个时刻突然有高并发访问可能会把系统拖垮
     * 这个时候使用预热机制，在前几秒只允许一点点的流量进来，预热之后才能允许指定的流量阈值访问
     *
     * 单机阈值 10
     * 预热时长 5
     *
     * 10 / 3 = 3 最开始只能有3qps，这个分母是默认配置
     * 经过5秒之后才会慢慢到达10qps
     *
     * 效果，开始疯狂f5,会有一部分请求被block，预热之后手速达不到10qps，就看不见了
     */
    @GetMapping("/warm-up")
    public String warmUp(){
        
        return "预热！";
    }
    
    
    /**
     * 对出超出限制的请求，将他放入队列中排队处理
     * 使用漏桶算法，所以没法应对突发流量，只能设置排队超时时间
     */
    @GetMapping("/queue")
    public String queue(){
        log.info("排队处理：{}",Thread.currentThread().getName());
        return "排队处理！";
    }
    
    
    
    
    /**
     * 限流触发后，走的内部降级方法
     */
    @GetMapping("/inner")
    @SentinelResource(value = "block-inner",blockHandler = "blockInnerBlockHandler")
    public String blockInner(){
        return "process ok by blockInner";
    }
    public String blockInnerBlockHandler(BlockException exception){
        return "blockInner is blocking!";
    }
    
    
    /**
     * 将限流触发的降级处理方法抽出来
     * 如果使用@SentinelReource注解配置资源
     * 在控制台上面要针对这个资源名称配置，他的blockHandler才有效
     * 其次，抽出来的方法必须是【静态的】
     */
    @GetMapping("/outer")
    @SentinelResource(value = "block-outer",blockHandlerClass = CustomBlockHandler.class,blockHandler = "handlerOuter")
    public String blockOuter(){
        return "process ok by blockOuter";
    }
    
    
}
