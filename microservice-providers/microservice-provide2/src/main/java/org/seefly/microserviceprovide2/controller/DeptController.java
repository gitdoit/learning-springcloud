package org.seefly.microserviceprovide2.controller;

import org.seefly.microserviceapi.entites.Dept;
import org.seefly.microserviceprovide2.service.DeptService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liujianxin
 * @date 2019/9/21 0:33
 */
@RestController
public class DeptController {
    private DeptService deptService;

    public DeptController(DeptService deptService) {
        this.deptService = deptService;
    }


    @PostMapping("dept/add")
    public boolean add(@RequestBody Dept dept) {
        return deptService.add(dept);
    }

    @GetMapping("dept/get/{id}")
    public Dept get(@PathVariable("id") Long id) {
        return deptService.get(id);
    }

    @GetMapping("dept/list")
    public List<Dept> list() {
        return deptService.list();
    }
}
