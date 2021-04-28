package org.seefly.provider.seata.service;

import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.seefly.microservice.provider.api.service.AccountFeignApi;
import org.seefly.microserviceapi.entites.OrderEntity;
import org.seefly.provider.seata.mapper.OrderMapper;
import org.seefly.provider.seata.mapper.StorageMapper;
import org.springframework.stereotype.Service;

/**
 * 订单\库存\账户三个表,本来应该分布在三个微服务中
 * 只是为了演示分布式事务,就用了两个来做
 * 订单\库存在本模块下,账户服务放在dept服务下
 *
 *
 * @author liujianxin
 * @date 2021/4/25 10:56
 */
@Slf4j
@Service
public class OrderService {
    private final OrderMapper orderMapper;
    private final StorageMapper storageMapper;
    private final AccountFeignApi accountFeignApi;
    
    
    public OrderService(OrderMapper orderMapper, StorageMapper storageMapper, AccountFeignApi accountFeignApi) {
        this.orderMapper = orderMapper;
        this.storageMapper = storageMapper;
        this.accountFeignApi = accountFeignApi;
    }
    
    @GlobalTransactional(name = "sss",rollbackFor = Exception.class)
    public String doOrder(OrderEntity orderEntity){
        log.info("1、开始创建订单...");
        orderMapper.create(orderEntity);
        
        log.info("2、开始扣减库存...");
        storageMapper.decrease(orderEntity.getProductId(),orderEntity.getCount());
        
        log.info("3、开始扣减余额...");
        accountFeignApi.decrease(orderEntity.getUserId(),orderEntity.getMoney());
        if(orderEntity.getCount() % 2 == 0){
            throw new RuntimeException("订单数量不能为偶数");
        }
        return "ok";
    }
}
