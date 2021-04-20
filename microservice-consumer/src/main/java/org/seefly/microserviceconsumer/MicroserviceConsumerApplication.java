package org.seefly.microserviceconsumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
// 扫秒指定包下的Feign客户端
@EnableFeignClients(basePackages = "org.seefly.microservice.provider.api.service")
// 调用端开启断路器
@EnableHystrix
// 对指定微服务使用自定义ribbon配置
// @RibbonClient(name = "microservicecloud-dept" ,configuration = RibbonConfig.class)
@Slf4j
public class MicroserviceConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceConsumerApplication.class, args);
    }

}
