package org.seefly.microserviceconsumer.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.seefly.microserviceapi.entites.Dept;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用restTemplate调用微服务接口
 * @author liujianxin
 * @date 2019/9/21 11:07
 */
@RestController
public class DeptControllerByRest {

    private final RestTemplate restTemplate;
    private final String url = "http://MICROSERVICECLOUD-DEPT";

    public DeptControllerByRest(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("consumer/dept/add")
    public boolean add(@RequestBody Dept dept) {
        return restTemplate.postForObject(url + "/dept/add", dept, Boolean.class);
    }

    @GetMapping("consumer/dept/get/{id}")
    public Dept get(@PathVariable("id") Long id) {
        return restTemplate.getForObject(url + "/dept/get/{id}", Dept.class, id);
    }
    
    @HystrixCommand(fallbackMethod = "errorList")
    @GetMapping("consumer/dept/list")
    public List<Dept> list() {
        return restTemplate.getForObject(url + "/dept/list", List.class);
    }
    
    public List<Dept> errorList() {
        System.out.println("fallback...list....");
        return new ArrayList<>();
    }
}
