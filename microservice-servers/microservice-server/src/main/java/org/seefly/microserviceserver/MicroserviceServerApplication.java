package org.seefly.microserviceserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class MicroserviceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroserviceServerApplication.class, args);
    }

}
