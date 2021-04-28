package org.seefly.provider.sentinel.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 被熔断之后的降级方法，这些方法必须是静态的，这就比较蛋疼了
 * 和Hystrix一样，这个抽出来和业务代码解耦
 * 肯定还有一个全局的BlockHandler
 * @author liujianxin
 * @date 2021/4/20 17:01
 */
@Slf4j
public class CustomBlockHandler {
    
    
    public static String handlerOuter(BlockException exception){
        return "response by handlerFoo";
    }
    
    
}
