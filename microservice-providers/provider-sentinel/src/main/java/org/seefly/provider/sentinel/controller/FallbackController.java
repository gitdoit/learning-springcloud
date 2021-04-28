package org.seefly.provider.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * fallback降级处理方式
 * sentinel支持三种降级方式
 *  1、异常比例
 *  2、异常数量
 *  3、慢掉比例
 * 如果一个资源同时配置了fallback和blockHandler，那么如果触发了限流就限流处理优先
 * @author liujianxin
 * @date 2021/4/23 10:35
 */
@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    
    /**
     * 异常比例
     *  统计时长：统计时间窗口，假设为10秒
     *  最小请求数：在这个窗口期间最少要有多少个请求，假设为6个
     *  比例阈值：0~1.0，“异常访”问占比，假设0.5
     *  熔断时长：熔断后休眠时间，假设5秒
     *
     * 多少秒的时间窗口内，调用了至少几次，其中发生了百分之多少的调用异常，就会触发多少秒的熔断
     * 进而直接走fallback，不再直接调用
     */
    @SentinelResource(value = "exceptionPercent",fallback = "handlerFallback")
    @GetMapping("/exception/{value}")
    public String exceptionPercent(@PathVariable("value") Integer value){
        if(value < 0){
            throw new IllegalArgumentException("！the value can not be negative!");
        }
        return "your input value is "+ value;
    }
    public String handlerFallback(@PathVariable("value")Integer value,Throwable throwable){
        log.info("Exception catch by Sentinel:{}",throwable.getLocalizedMessage());
        return "fallback by sentinel,because the value is "+ value;
    }
    
    /**
     * 慢调比例
     *  统计时长：统计时间窗口，假设为10秒
     *  最小请求数：在这个窗口期间最少要有多少个请求，假设为6个
     *  最大RT，每个请求的最大响应时间，超出这个时间就视为一个“异常访问”，假设200ms
     *  比例阈值：0~1.0，“异常访”问占比，假设0.5
     *  熔断时长：熔断后休眠时间，假设5秒
     * 总结就是：
     *  在一个10秒的统计窗口内，最少要有6个请求进来，并且其中一半的请求响应时间超过了200ms，这个时候就开启了熔断降级；
     *  并且在5秒后再次尝试调用目标服务，RT<200ms就恢复
     *
     *  还有一个异常数，忽略
     */
    @GetMapping("/slow-call/{duration}")
    @SentinelResource(value = "slowCall",fallback = "slowCallFallback")
    public String slowCall(@PathVariable("duration") Long duration) throws InterruptedException {
        if(duration < 0){
            throw new IllegalArgumentException("Sleep duration can not be negative!");
        }
        Thread.sleep(duration);
        return "sleeping well!";
    }
    public String slowCallFallback(@PathVariable("duration") Long duration,Throwable throwable){
        log.info("Sleeping fallback !!!");
        return "Sleeping shit!!";
    }
}
