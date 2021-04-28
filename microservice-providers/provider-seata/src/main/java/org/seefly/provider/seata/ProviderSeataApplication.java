package org.seefly.provider.seata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

// https://zhuanlan.zhihu.com/p/35616810
// https://www.techgrow.cn/posts/e8b71fbe.html#Seata-Server-%E9%85%8D%E7%BD%AE%E4%BB%8B%E7%BB%8D
// 扫秒指定包下的Feign客户端
@EnableFeignClients(basePackages = "org.seefly.microservice.provider.api.service")
@EnableEurekaClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ProviderSeataApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProviderSeataApplication.class, args);
    }

}
