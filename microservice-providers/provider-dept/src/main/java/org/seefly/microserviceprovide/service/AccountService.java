package org.seefly.microserviceprovide.service;

import java.math.BigDecimal;

public interface AccountService {
    String decrease(Long userId, BigDecimal money);
    
}
