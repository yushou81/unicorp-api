package com.csu.unicorp.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 聊天会话视图对象
 */
@Data
public class ChatSessionVO {
    
    /**
     * 会话ID
     */
    private Long id;
    
    /**
     * 对话用户ID
     */
    private Long userId;
    
    /**
     * 对话用户名称
     */
    private String userName;
    
    /**
     * 对话用户头像
     */
    private String userAvatar;
    
    /**
     * 最新消息内容
     */
    private String lastMessage;
    
    /**
     * 最新消息时间
     */
    private LocalDateTime lastMessageTime;
    
    /**
     * 未读消息数
     */
    private Integer unreadCount;
}