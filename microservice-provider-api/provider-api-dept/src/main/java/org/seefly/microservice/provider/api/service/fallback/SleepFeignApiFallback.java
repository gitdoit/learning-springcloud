package org.seefly.microservice.provider.api.service.fallback;

import org.seefly.microservice.provider.api.service.SleepFeignApi;

/**
 * @author liujianxin
 * @date 2021/4/14 16:03
 */
public class SleepFeignApiFallback implements SleepFeignApi {
    
    @Override
    public String sleep(Long duration) {
        return Thread.currentThread().getName()+":sleep  fallback!!!";
    }
    
    @Override
    public String sleepForSelf(Long duration) {
        return Thread.currentThread().getName()+":sleepForSelf  fallback!!!";
    }
}
