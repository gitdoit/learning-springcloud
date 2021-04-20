package org.seefly.microserviceconsumer.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liujianxin
 * @date 2021/4/14 22:31
 */
@Configuration
public class FeignConfig {
    
    
    /**
     * 配置日志
     * 对于使用Feign客户端调用的方式,日志记录调用过程
     * 配合
     * logging:
     *   level:
     *     org.seefly.microservice.provider.api.service.SleepFeignApi: debug
     */
    @Bean
    public Logger.Level fullLog(){
        return Logger.Level.FULL;
    }
}
