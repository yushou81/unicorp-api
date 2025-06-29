package com.csu.unicorp.vo;

import java.time.LocalDateTime;

import com.csu.unicorp.dto.ChatMessageDTO;

import lombok.Data;

/**
 * 聊天消息视图对象
 */
@Data
public class ChatMessageVO {
    
    /**
     * 消息ID
     */
    private Long id;
    
    /**
     * 会话ID
     */
    private Long sessionId;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 发送者名称
     */
    private String senderName;
    
    /**
     * 消息类型
     */
    private ChatMessageDTO.MessageType type;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 发送时间
     */
    private LocalDateTime sentAt;
    
    /**
     * 是否已读
     */
    private Integer isRead;
} 