package org.seefly.microservice.provider.api.service;

import org.seefly.microservice.provider.api.service.fallback.AccountFeignApiFallback;
import org.seefly.microserviceapi.web.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author liujianxin
 * @date 2021/4/25 11:00
 */
@FeignClient(name = "microservicecloud-dept",contextId = "account",fallback = AccountFeignApiFallback.class)
public interface AccountFeignApi {
    
    @PutMapping("/account/decrease")
     BaseResponse<String> decrease(@RequestParam("userId") Long userId, @RequestParam("money") BigDecimal money);
}
