package org.seefly.microserviceprovide2.dao;

import org.seefly.microserviceapi.entites.Dept;

import java.util.List;

public interface DeptDao {

    boolean addDept(Dept dept);

    Dept findById(Long id);

    List<Dept> findAll();


}
