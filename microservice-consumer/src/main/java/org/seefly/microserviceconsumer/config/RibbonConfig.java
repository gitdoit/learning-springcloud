package org.seefly.microserviceconsumer.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.seefly.microserviceconsumer.rule.MyRoundRibbonRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liujianxin
 * @date 2021/4/14 14:48
 */
@Configuration
public class RibbonConfig {
    
    @Bean
    public IRule myRule(){
        return new MyRoundRibbonRule();
    }
    
    
    
    
    /**
     * 负载均衡算法
     * 系统自带随机算法,就是用Random函数来随机取一个可用的实例
     */
    //@Bean
    public IRule randomRule(){
        return new RandomRule();
    }
    
    /**
     * 系统自带轮询算法
     */
    public IRule RoundRibbonRule(){
        return new RoundRobinRule();
    }

}
