package org.seefly.microserviceprovide;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.util.function.Predicate;
import java.util.stream.Stream;

@MapperScan("org.seefly.microserviceprovide.dao")
@SpringBootApplication
@EnableEurekaClient
public class MicroserviceProvideApplication {

    public static void main(String[] args) {
        Stream.of(1,2,3).filter(((Predicate<Integer>) (t -> t >1)).and(t -> t < 20));

        SpringApplication.run(MicroserviceProvideApplication.class, args);
    }

}
