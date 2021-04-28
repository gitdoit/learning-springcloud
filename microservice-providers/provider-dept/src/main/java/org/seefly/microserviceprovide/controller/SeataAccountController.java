package org.seefly.microserviceprovide.controller;

import org.seefly.microserviceapi.web.BaseResponse;
import org.seefly.microserviceprovide.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author liujianxin
 * @date 2021/4/25 14:18
 */
@RestController
@RequestMapping("/account")
public class SeataAccountController {
    private final AccountService accountService;
    
    public SeataAccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    
    @PutMapping("/decrease")
    public BaseResponse<String> decrease(Long userId, BigDecimal money){
        String decrease = accountService.decrease(userId, money);
        return BaseResponse.ok(decrease);
    }
    
    
}
