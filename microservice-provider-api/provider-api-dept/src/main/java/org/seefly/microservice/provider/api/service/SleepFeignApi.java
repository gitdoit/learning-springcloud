package org.seefly.microservice.provider.api.service;


import org.seefly.microservice.provider.api.config.CustomFeignClientsConfiguration;
import org.seefly.microservice.provider.api.service.fallback.SleepFeignApiFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "microservicecloud-dept",contextId = "sleep",configuration = CustomFeignClientsConfiguration.class,fallback = SleepFeignApiFallback.class)
public interface SleepFeignApi {
    
    
    @RequestMapping(value = "sleep/for/{duration}",method = RequestMethod.GET)
    String sleep(@PathVariable("duration") Long duration);
    
    @RequestMapping(value = "sleep/server-side/{duration}",method = RequestMethod.GET)
    String sleepForSelf(@PathVariable("duration") Long duration);
}
