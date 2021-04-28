package org.seefly.microserviceapi.entites;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author liujianxin
 * @date 2021/4/25 10:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntity {
    private Long id;
    /*用户id*/
    private Long userId;
    /*总金额*/
    private BigDecimal total;
    /*已用金额*/
    private BigDecimal used;
    /*未用金额*/
    private BigDecimal residue;

}
