package org.seefly.microserviceconsumer.controller;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.shared.Applications;
import org.seefly.microserviceapi.entites.Dept;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author liujianxin
 * @date 2019/9/21 11:07
 */
@RestController
public class DeptControllerConsumer {

    private final RestTemplate restTemplate;
    private final String url = "http://MICROSERVICECLOUD-DEPT";

    public DeptControllerConsumer(RestTemplate restTemplate) {
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

    @GetMapping("consumer/dept/list")
    public List<Dept> list() {
        return restTemplate.getForObject(url + "/dept/list", List.class);
    }
}
