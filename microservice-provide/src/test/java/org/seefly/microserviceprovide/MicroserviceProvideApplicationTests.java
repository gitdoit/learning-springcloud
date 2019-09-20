package org.seefly.microserviceprovide;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seefly.microserviceapi.entites.Dept;
import org.seefly.microserviceprovide.dao.DeptDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MicroserviceProvideApplicationTests {

    @Autowired
    private DeptDao deptDao;


    @Test
    public void contextLoads() {
        List<Dept> all = deptDao.findAll();
        System.out.println(all.size());
    }

}
