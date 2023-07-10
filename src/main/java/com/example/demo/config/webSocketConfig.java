package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class webSocketConfig {

    @Bean
    // 注入一个ServerEndpointExporter类的 bean对象，自动注册 使用了带有 @ServerEndpoint该注解的bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
