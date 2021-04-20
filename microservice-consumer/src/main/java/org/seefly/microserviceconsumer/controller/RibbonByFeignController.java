package org.seefly.microserviceconsumer.controller;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.TimedSupervisorTask;
import org.seefly.microservice.provider.api.service.DeptFeignApi;
import org.seefly.microservice.provider.api.service.fallback.DeptFeignApiFallBack;
import org.seefly.microserviceapi.entites.Dept;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 演示Feign配合Ribbon进行负载均衡
 * @author liujianxin
 * @date 2020/8/18 15:48
 */
@Import(DeptFeignApiFallBack.class)
@RestController
public class RibbonByFeignController {
    private final DeptFeignApi deptFeignApi;

    public RibbonByFeignController(DeptFeignApi deptFeignApi) {
        this.deptFeignApi = deptFeignApi;
    }
    @GetMapping("feign/all")
    public List<Dept> getAll(){
        return deptFeignApi.list();
    }

    @PostMapping("feign/add")
    public void add(@RequestBody Dept dept){
        deptFeignApi.add(dept);
    }

    @GetMapping("feign/dept/{id}")
    public Dept get(@PathVariable("id") Long id) {
        return deptFeignApi.get(id);
    }
    



}
