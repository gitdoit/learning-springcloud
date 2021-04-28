package org.seefly.microserviceprovide.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author liujianxin
 * @date 2021/4/27 10:25
 */
@Configuration
@MapperScan("org.seefly.microserviceprovide.dao")
public class MyBatisConfig {
    
    
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }
    
    @Bean
    public SqlSessionFactory sqlSessionFactoryBean(DataSource druidDataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        bean.setMapperLocations(resourceResolver.getResources("classpath:mybatis/mapper/**/*.xml"));
        bean.setDataSource(druidDataSource);
        return bean.getObject();
    }
    
    
}
