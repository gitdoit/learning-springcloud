package org.seefly.provider.seata.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.sql.DataSource;

/**
 * @author liujianxin
 * @date 2021/4/25 18:02
 */
@MapperScan("org.seefly.provider.seata.mapper")
@Configuration
public class MyBatisConfig {
    
    
        private String mapperLocations;
        
        @Bean
        @ConfigurationProperties(prefix = "spring.datasource")
        public DataSource druidDataSource() {
            return new DruidDataSource();
        }
        
//        @Bean
//        public DataSourceProxy dataSourceProxy(DataSource druidDataSource) {
//            return new DataSourceProxy(druidDataSource);
//        }
        
        @Bean
        public SqlSessionFactory sqlSessionFactoryBean(DataSource druidDataSource) throws Exception {
            SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
            bean.setDataSource(druidDataSource);
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            return bean.getObject();
        }
    
    
}
