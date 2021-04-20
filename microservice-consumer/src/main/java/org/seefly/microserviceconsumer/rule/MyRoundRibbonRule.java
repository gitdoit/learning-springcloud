package org.seefly.microserviceconsumer.rule;


import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * 自定义轮询算法
 * 如果我在家里,就调用公网服务,在公司,就可以同时调用公司的服务
 * @author liujianxin
 * @date 2021/4/14 15:13
 */
@Slf4j
public class MyRoundRibbonRule extends AbstractLoadBalancerRule {
    private boolean isHome = false;
    private IRule rule;
    
    
    public MyRoundRibbonRule() {
        this.rule = new RoundRobinRule();
    
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            if(hostAddress.startsWith("192.168.10")){
                isHome = false;
            }else {
                isHome = true;
            }
            log.info("负载均,当前是否家里的环境[{}]",isHome);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
    
    }
    
    /**
     * 乞丐版轮询
     * 如果是家里的环境 就不要访问公司的实例,直接访问华为云的
     */
    @Override
    public Server choose(Object key) {
        
        ILoadBalancer loadBalancer = getLoadBalancer();
        List<Server> reachableServers = loadBalancer.getReachableServers();
        int limit = 0;
        Server next;
        do {
            limit++;
            next = rule.choose(key);
        }while (!canUse(next) && limit < reachableServers.size());
        
        if(!canUse(next)){
            throw new RuntimeException("当前环境,没有可用的实例!");
        }
        return next;
    }
    
    private boolean canUse(Server server){
        if(server == null){
            return false;
        }
        String host = server.getHost();
        if(isHome){
            return host.startsWith("121");
        }
        return true;
    }
    
    
    @Override
    public void setLoadBalancer(ILoadBalancer lb) {
        super.setLoadBalancer(lb);
        rule.setLoadBalancer(lb);
    }
}
