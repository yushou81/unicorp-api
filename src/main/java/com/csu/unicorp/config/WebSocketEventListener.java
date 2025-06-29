package com.csu.unicorp.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket事件监听器
 * 用于记录WebSocket连接、断开等事件
 */
@Component
@Slf4j
public class WebSocketEventListener {

    /**
     * 监听客户端连接请求
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("收到WebSocket连接请求: {}", headerAccessor);
        
        // 获取用户信息
        Authentication auth = (Authentication) headerAccessor.getUser();
        if (auth != null) {
            log.info("用户 [{}] 尝试建立WebSocket连接", auth.getName());
        } else {
            log.info("匿名用户尝试建立WebSocket连接");
        }
    }

    /**
     * 监听连接成功事件
     */
    @EventListener
    public void handleWebSocketConnected(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("WebSocket连接建立成功: {}", headerAccessor.getSessionId());
        
        Authentication auth = (Authentication) headerAccessor.getUser();
        if (auth != null) {
            log.info("用户 [{}] 已成功连接WebSocket，Principal类型: {}", auth.getName(), auth.getClass().getName());
        } else {
            log.info("匿名用户已成功连接WebSocket");
        }
    }
    
    /**
     * 监听订阅事件
     */
    @EventListener
    public void handleWebSocketSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("WebSocket订阅: {} - 目标: {}", headerAccessor.getSessionId(), headerAccessor.getDestination());
        
        Authentication auth = (Authentication) headerAccessor.getUser();
        if (auth != null) {
            log.info("用户 [{}] 订阅了 {}", auth.getName(), headerAccessor.getDestination());
            log.info("订阅用户Principal详情: name={}, class={}", auth.getName(), auth.getPrincipal().getClass().getName());
        } else {
            log.warn("匿名用户尝试订阅 {}", headerAccessor.getDestination());
        }
    }

    /**
     * 监听断开连接事件
     */
    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("WebSocket连接断开: {}", headerAccessor.getSessionId());
        
        Authentication auth = (Authentication) headerAccessor.getUser();
        if (auth != null) {
            log.info("用户 [{}] 已断开WebSocket连接", auth.getName());
        } else {
            log.info("匿名用户已断开WebSocket连接");
        }
    }
} 