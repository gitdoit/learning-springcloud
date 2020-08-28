package org.seefly.microservice.provider.api.config;

import feign.FeignException;
import feign.Response;
import feign.Retryer;
import feign.codec.Decoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.cloud.netflix.feign.support.ResponseEntityDecoder;
import org.springframework.cloud.netflix.feign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.lang.reflect.Type;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author liujianxin
 * @date 2020/8/28 10:05
 */
@Configuration
public class CustomFeignClientsConfiguration {
    @Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;
    
    
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(100, SECONDS.toMillis(1), 5);
    }
    
    @Bean
    public Decoder feignDecoder() {
        return new ResponseEntityDecoder(new MyDecoder(this.messageConverters){
        
        });
    }
    
    public static class MyDecoder extends SpringDecoder{
    
        public MyDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
            super(messageConverters);
        }
    
        @Override
        public Object decode(Response response, Type type) throws IOException, FeignException {
            Object decode = super.decode(response, type);
            System.out.println("自定义解码器来一脚...");
            return decode;
        }
    }
    
}
