package org.seefly.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liujianxin
 * @date 2021/4/17 16:43
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator baidu(RouteLocatorBuilder builder){
        RouteLocatorBuilder.Builder routes = builder.routes();
        return routes.route("baidu", r -> r.path("/guonei").uri("http://news.baidu.com/")).build();
    }
}
