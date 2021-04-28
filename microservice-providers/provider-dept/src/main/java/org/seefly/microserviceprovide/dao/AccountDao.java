package org.seefly.microserviceprovide.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 *
 * CREATE TABLE t_account(
 *     id BIGINT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY ,
 *     user_id BIGINT(11) DEFAULT NULL COMMENT '用户id',
 *     total DECIMAL(10,0) DEFAULT NULL COMMENT '总额度',
 *     used DECIMAL(10,0) DEFAULT NULL COMMENT '已用额度',
 *     residue DECIMAL(10,0) DEFAULT 0 COMMENT '剩余可用额度'
 * )ENGINE=InnoDB AUTO_INCREMENT=7 CHARSET=utf8;
 * INSERT INTO t_account(id, user_id, total, used, residue) VALUES(1,1,1000,0,1000);
 *
 * @author liujianxin
 * @date 2021/4/25 10:27
 */
@Mapper
public interface AccountDao {
    
    void decrease(@Param("userId") Long userId, @Param("money") BigDecimal money);
}
