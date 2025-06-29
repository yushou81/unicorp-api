package com.csu.unicorp.dto;

import lombok.Data;

/**
 * 聊天消息传输对象
 */
@Data
public class ChatMessageDTO {
    
    /**
     * 消息类型：CHAT(聊天消息), JOIN(加入会话), LEAVE(离开会话)
     */
    private MessageType type;
    
    /**
     * 接收者ID
     */
    private Long receiverId;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型枚举
     */
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
} 