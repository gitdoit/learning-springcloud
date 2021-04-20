package org.seefly.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author liujianxin
 * @date 2021/4/17 22:14
 */
@Component
public class UserNameCheckFilter implements GlobalFilter, Ordered {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String username = exchange.getRequest().getQueryParams().getFirst("username");
        if(StringUtils.isEmpty(username)){
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode("没有用户名!");
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(byteBuffer);; // wrapping only, no allocation
            exchange.getResponse().getHeaders().setContentLength(byteBuffer.remaining());
            exchange.getResponse().getHeaders().add("Content-Type","text/plain;charset=UTF-8");
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
        return chain.filter(exchange);
    }
    
    /**
     * 过滤器优先级
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
