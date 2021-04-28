package org.seefly.microserviceprovide.dao;

import org.apache.ibatis.annotations.Mapper;
import org.seefly.microserviceapi.entites.Dept;

import java.util.List;

@Mapper
public interface DeptDao {

    boolean addDept(Dept dept);

    Dept findById(Long id);

    List<Dept> findAll();


}
