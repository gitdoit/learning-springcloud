package org.seefly.microservice.provider.api.service.fallback;

import org.seefly.microservice.provider.api.service.AccountFeignApi;
import org.seefly.microserviceapi.web.BaseResponse;

import java.math.BigDecimal;

/**
 * @author liujianxin
 * @date 2021/4/25 11:01
 */
public class AccountFeignApiFallback implements AccountFeignApi {
    
    @Override
    public BaseResponse<String> decrease(Long userId, BigDecimal money) {
        return BaseResponse.fail("Fallback:余额扣减失败!!!");
    }
}
