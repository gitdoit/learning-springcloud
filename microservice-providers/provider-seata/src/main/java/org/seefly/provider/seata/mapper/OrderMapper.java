package org.seefly.provider.seata.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.seefly.microserviceapi.entites.OrderEntity;
import org.springframework.stereotype.Repository;

/**
 * 订单表
 * @author liujianxin
 * @date 2021/4/25 10:33
 */
@Repository
@Mapper
public interface OrderMapper {
    
    /**
     * 创建订单
     */
    @Insert("insert into t_order(id,user_id,product_id,count,money,status) values(null,#{userId},#{productId},#{count},#{money},0)")
    void create(OrderEntity orderEntity);
    
    /**
     * 修改订单状态,0-->1
     */
    @Update("update t_order set status = 1 where user_id = #{userId} and status = #{status}")
    void update(@Param("userId") Long userId,@Param("status")Integer status);
}
