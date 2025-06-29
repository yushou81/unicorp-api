package com.csu.unicorp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket配置
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 设置消息代理的前缀
        registry.enableSimpleBroker("/topic", "/queue");
        
        // 设置应用的前缀
        registry.setApplicationDestinationPrefixes("/app");
        // 设置用户订阅点前缀
        registry.setUserDestinationPrefix("/user");
        
        log.info("配置消息代理: /topic, /queue, 应用前缀: /app, 用户前缀: /user");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册STOMP端点
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // 允许所有来源访问
                .withSockJS(); // 启用SockJS回退支持
        
        log.info("注册WebSocket端点: /ws (含SockJS支持)");
    }
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 添加认证拦截器
        registration.interceptors(webSocketAuthInterceptor);
        log.info("配置WebSocket认证拦截器");
    }
} 