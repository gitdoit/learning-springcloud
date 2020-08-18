package org.seefly.microserviceconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages = "org.seefly.microservice.provider.api.service")
public class MicroserviceConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceConsumerApplication.class, args);
    }

}
