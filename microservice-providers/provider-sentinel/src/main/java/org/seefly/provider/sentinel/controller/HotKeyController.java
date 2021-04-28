package org.seefly.provider.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liujianxin
 * @date 2021/4/20 16:02
 */
@Slf4j
@RestController
@RequestMapping("/hotkey")
public class HotKeyController {
    
    /**
     * 热点key限制
     *  比如说商品查询接口，商品名称参数项，对他进行限制，每秒限制查询多少次
     *
     * sentinel把一切都看作资源，这个接口也是资源，service的方法也是资源(跟Hystrix一样。。。)
     * 资源都有名字就是value指定，降级处理方式就是blockHandler
     *
     * 例外：针对参数值为指定的值，单独配置 限流qps
     * 比如一开始一刀切的方式，对商品名称查询参数进行限制，输入什么每秒都是1qps。我们针对 手机壳 这个入参配置例外
     */
    @GetMapping("/goods")
    @SentinelResource(value = "good-search",blockHandler = "goodsBlockHandler")
    public String hotKye(String p1,String p2){
        log.info("热点key，根据参数p1{},p2{}查询",p1,p2);
        return "Goods named xxx was searched by your param!";
    }
    
    public String goodsBlockHandler(String p1,String p2, BlockException exception){
        log.error("商品查询接口被阻塞：{}",exception.getLocalizedMessage());
        return "you are fired!";
    }
}
