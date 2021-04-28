package org.seefly.microserviceapi.entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author liujianxin
 * @date 2021/4/25 10:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer count;
    private BigDecimal money;
    private Integer status;
}
