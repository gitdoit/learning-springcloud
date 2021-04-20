package org.seefly.microserviceprovide.service.impl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.seefly.microserviceapi.entites.Dept;
import org.seefly.microserviceprovide.dao.DeptDao;
import org.seefly.microserviceprovide.service.DeptService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liujianxin
 * @date 2019/9/21 0:30
 */
@Service
public class DeptServiceImpl implements DeptService {

    private DeptDao deptDao;

    public DeptServiceImpl(DeptDao deptDao) {
        this.deptDao = deptDao;
    }


    @Override
    public Dept get(Long id) {
        return deptDao.findById(id);
    }

    @Override
    public boolean add(Dept dept) {
        return deptDao.addDept(dept);
    }
    
    @HystrixCommand(
            
            threadPoolKey="6666",
            commandProperties = {
                    // 超时时间
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "2000"),
                    // 请求次数
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "5"),
                    // 窗口大小
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "5000"),
                    // 失败率
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"),
                
            })
    @Override
    public List<Dept> list() {
        return deptDao.findAll();
    }
}
