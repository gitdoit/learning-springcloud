package top.seefly.nacosdemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class NacosDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(NacosDemoApplication.class, args);
    }

    @RestController
    @RequestMapping("config")
    @RefreshScope
    public static class Controller{
        @Value(value = "${useLocalCache:false}")
        private boolean useLocalCache;
        @Value(value = "${some-words:default}")
        private String someWords;
        @Value(value = "${actuator:no}")
        private String actuator;



        @GetMapping("/get")
        public boolean get() {
            return useLocalCache;
        }
        @GetMapping("/words")
        public String getSomeWords(){
            return someWords;
        }

        @GetMapping("/actuator")
        public String getActuator(){
            return actuator+this;
        }
    }

    @Component
    public static class EventListeners{
        @EventListener
        public void envListener(EnvironmentChangeEvent event) {
            System.out.println(event);
        }
    }

}
