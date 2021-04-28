package org.seefly.provider.sentinel;

import org.seefly.microservice.provider.api.service.fallback.SleepFeignApiFallback;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * <a href="https://github.com/alibaba/Sentinel/wiki/%E5%A6%82%E4%BD%95%E4%BD%BF%E7%94%A8">说明<a/>
 */
@SpringBootApplication
@EnableEurekaClient
// 扫秒指定包下的Feign客户端
@EnableFeignClients(basePackages = "org.seefly.microservice.provider.api.service")

public class ProviderSentinelApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProviderSentinelApplication.class, args);
    }
    
}
