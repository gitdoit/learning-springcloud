package org.seefly.microserviceprovide.service.impl;

import io.seata.spring.annotation.GlobalTransactional;
import org.seefly.microserviceprovide.dao.AccountDao;
import org.seefly.microserviceprovide.service.AccountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author liujianxin
 * @date 2021/4/25 14:15
 */
@Service
public class AccountServiceImpl implements AccountService {
    private final AccountDao accountDao;
    
    public AccountServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }
    
    @Override
    @GlobalTransactional(name = "sss",rollbackFor = Exception.class)
    public String decrease(Long userId, BigDecimal money){
        if(money.intValue() % 2 == 0){
            throw new RuntimeException("我不喜欢偶数!");
        }
        accountDao.decrease(userId,money);
        return "ok!";
    }
}
