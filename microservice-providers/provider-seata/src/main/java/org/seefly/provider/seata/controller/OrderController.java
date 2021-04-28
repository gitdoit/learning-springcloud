package org.seefly.provider.seata.controller;

import org.seefly.microserviceapi.entites.OrderEntity;
import org.seefly.microserviceapi.web.BaseResponse;
import org.seefly.provider.seata.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liujianxin
 * @date 2021/4/25 10:14
 */
@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    
    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody OrderEntity order){
        orderService.doOrder(order);
        return BaseResponse.ok("下单成功!");
    }
    // bash nacos-config-push.sh -h 121.36.142.5 -p 8848 -g SEATA_GROUP -t seata_namespace_id -u nacos -w nacos
}
