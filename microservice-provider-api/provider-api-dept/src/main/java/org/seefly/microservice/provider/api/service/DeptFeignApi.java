package org.seefly.microservice.provider.api.service;

import org.seefly.microserviceapi.entites.Dept;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "microservicecloud-dept")
public interface DeptFeignApi {

    @RequestMapping(value = "dept/add",method = RequestMethod.POST)
    boolean add(@RequestBody Dept dept);


    @RequestMapping(value = "dept/get/{id}",method = RequestMethod.GET)
    Dept get(@PathVariable("id") Long id);

    @RequestMapping(value = "dept/list",method = RequestMethod.GET)
    List<Dept> list();

}
