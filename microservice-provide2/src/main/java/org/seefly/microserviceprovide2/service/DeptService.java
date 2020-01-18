package org.seefly.microserviceprovide2.service;

import org.seefly.microserviceapi.entites.Dept;

import java.util.List;

public interface DeptService {

    Dept get(Long id);

    boolean add(Dept dept);

    List<Dept> list();
}
