package com.csu.unicorp.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("chat_message")
public class ChatMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sessionId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;
    private Boolean isRead;
}
