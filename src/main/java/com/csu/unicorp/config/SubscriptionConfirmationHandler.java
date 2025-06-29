package com.csu.unicorp.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.csu.unicorp.vo.ChatMessageVO;

import lombok.extern.slf4j.Slf4j;

/**
 * 订阅确认处理器
 * 当用户订阅消息队列时发送确认消息
 */
@Component
@Slf4j
public class SubscriptionConfirmationHandler implements ApplicationListener<SessionSubscribeEvent> {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public SubscriptionConfirmationHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        
        // ��������Ϣ���ж���
        if (destination != null && destination.startsWith("/user/") && destination.endsWith("/queue/messages")) {
            Authentication auth = (Authentication) headerAccessor.getUser();
            if (auth != null) {
                String userId = auth.getName();
                log.info("用户 [{}] 成功订阅消息队列，发送确认消息", userId);
                
                // ����ȷ����Ϣ
                ChatMessageVO confirmMessage = new ChatMessageVO();
                confirmMessage.setContent("您已成功订阅消息队列");
                confirmMessage.setSenderName("系统");
                confirmMessage.setSentAt(LocalDateTime.now());
                
                try {
                    // 发送确认消息
                    log.info("用户 [{}] 发送确认消息", userId);
                    messagingTemplate.convertAndSendToUser(
                            userId,
                            "/queue/messages",
                            confirmMessage
                    );
                    log.info("确认消息已发送");
                } catch (Exception e) {
                    log.error("发送确认消息失败: {}", e.getMessage(), e);
                }
            } else {
                log.warn("匿名用户尝试订阅消息队列");
            }
        }
    }
}
