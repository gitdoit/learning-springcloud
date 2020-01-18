package org.seefly.microserviceprovide2.service.impl;

import org.seefly.microserviceapi.entites.Dept;
import org.seefly.microserviceprovide2.dao.DeptDao;
import org.seefly.microserviceprovide2.service.DeptService;
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

    @Override
    public List<Dept> list() {
        return deptDao.findAll();
    }
}
