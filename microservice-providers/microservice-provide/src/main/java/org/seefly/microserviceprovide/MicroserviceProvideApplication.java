package org.seefly.microserviceprovide;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@MapperScan("org.seefly.microserviceprovide.dao")
@SpringBootApplication
@EnableEurekaClient
public class MicroserviceProvideApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceProvideApplication.class, args);
    }

}
