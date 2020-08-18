package org.seefly.microserviceprovide2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@MapperScan("org.seefly.microserviceprovide2.dao")
public class MicroserviceProvide2Application {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceProvide2Application.class, args);
    }

}
