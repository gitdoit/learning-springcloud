package org.seefly.microservice.provider.api.service.fallback;

import org.seefly.microservice.provider.api.service.DeptFeignApi;
import org.seefly.microserviceapi.entites.Dept;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liujianxin
 * @date 2020/8/28 14:12
 */
public class DeptFeignApiFallBack implements DeptFeignApi {
    
    @Override
    public boolean add(Dept dept) {
        System.out.println("can not add dept...");
        return false;
    }
    
    @Override
    public Dept get(Long id) {
        System.out.println("can not get dept by id,fallback");
        return new Dept();
    }
    
    @Override
    public List<Dept> list() {
        System.out.println("can not get dept list,fallback...");
        return new ArrayList<>();
    }
}
