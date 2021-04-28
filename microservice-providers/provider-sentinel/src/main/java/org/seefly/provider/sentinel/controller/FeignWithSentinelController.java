package org.seefly.provider.sentinel.controller;

import org.seefly.microservice.provider.api.service.SleepFeignApi;
import org.seefly.microservice.provider.api.service.fallback.SleepFeignApiFallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liujianxin
 * @date 2021/4/23 15:01
 */
@Import(SleepFeignApiFallback.class)
@RestController
@RequestMapping("/feign")
public class FeignWithSentinelController {
    @Autowired
    private SleepFeignApi sleepFeignApi;
    
    @GetMapping("/sleep/{duration}")
    public String sleep(@PathVariable("duration") Long duration){
        String sleep = sleepFeignApi.sleep(duration);
        return sleep;
    }
    
}
