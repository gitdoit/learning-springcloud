package org.seefly.microserviceconsumer.controller;

import org.seefly.microservice.provider.api.service.DeptFeignApi;
import org.seefly.microserviceapi.entites.Dept;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 使用Feign的方式调用微服务
 * @author liujianxin
 * @date 2020/8/18 15:48
 */
@RestController
public class DeptControllerByFeign {
    private final DeptFeignApi deptFeignApi;

    public DeptControllerByFeign(DeptFeignApi deptFeignApi) {
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
