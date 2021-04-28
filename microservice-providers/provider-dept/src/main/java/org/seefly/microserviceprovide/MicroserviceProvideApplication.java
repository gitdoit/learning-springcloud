package org.seefly.microserviceprovide;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;


@EnableEurekaClient
// 开启服务提供方的熔断功能
@EnableCircuitBreaker
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MicroserviceProvideApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceProvideApplication.class, args);
    }

}
