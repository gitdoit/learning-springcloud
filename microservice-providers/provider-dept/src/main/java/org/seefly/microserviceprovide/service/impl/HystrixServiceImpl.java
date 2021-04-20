package org.seefly.microserviceprovide.service.impl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.seefly.microserviceprovide.service.SleepService;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.LockSupport;

/**
 * 服务端的断路器
 * 1、配置类上添加 @EnableCircuitBreaker开启断路器功能
 * 2、目标方法上标注 @HystrixCommand 开启断路保护
 *
 * 逻辑
 *  使用断路器线程池调用目标方法
 *  如果目标方法发生异常，使用相同的线程调用备选方案
 *  如果目标方法超时，使用新的线程池线程调用备选方案
 *
 *  如果调用目标方法失败次数超过阈值，此时断路器打开，后续调用直接走备选方案
 *  经过一段时间，断路器恢复到半开状态，此时会放入一部分请求到目标方法
 *      如果成功，断路器关闭
 *      如果失败，重新回到打开状态，重复上述步骤
 *
 *  验证
 *     对下面的接口疯狂调用，传入-1.n次之后就触发断路器 直接走备选方案了
 *
 *
 *
 * @author liujianxin
 * @date 2021/4/15 11:00
 */
@Slf4j
@Service
public class HystrixServiceImpl implements SleepService {
    
    /**
     * 测试服务提供方熔断策略
     *
     * 当执行时间超过2秒 \或者发生了异常 就直接执行降级逻辑 返回备选内容
     * print:
     *  100-->
     *      hystrix-SleepServiceImpl-5:sleepTimeout-->100
     *  -2--->
     *      hystrix-SleepServiceImpl-6:sleepTimeoutFallback:系统失败,稍后重试!
     *  2100-->
     *      HystrixTimer-4:sleepTimeoutFallback:系统失败,稍后重试!
     *
     * 可以看到,对于业务方法会使用线程池来执行,当抛出异常的时候会使用相同的线程处理,如果超时,直接中断使用另一个线程池调用备选
     */
    @HystrixCommand(
            // 回退方法
            fallbackMethod = "sleepTimeoutFallback",
            commandProperties = {
                    // 超时时间
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "2000"),
                    // 请求次数
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "5"),
                    // 熔断休眠时间，熔断触发5秒后，进行半开尝试
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "5000"),
                    // 失败率
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"),
                    
            })
    public String sleepTimeout(long duration){
        log.info("[sleepTimeout][{}]将休眠{}ns",Thread.currentThread().getName(),duration);
        if (duration < 0){
            throw new RuntimeException("参数设置错误!");
        }
         // 超过两秒 hystrix执行中断
        // Thread.sleep(duration);
        
        // 我用这个挂起来 1ms = 1000000ns
        LockSupport.parkNanos(duration);
        log.info("起来了,中断位{}",Thread.currentThread().isInterrupted());
        return Thread.currentThread().getName()+":sleepTimeout-->"+duration;
    }
    
    /**
     * 当目标方法执行异常或者超时时调用备选方案
     * @param duration
     * @return
     */
    public String sleepTimeoutFallback(long duration){
        return Thread.currentThread().getName()+":sleepTimeoutFallback:系统失败,稍后重试!";
    }
    
}
