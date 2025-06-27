package com.csu.unicorp.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("chat_session")
public class ChatSession {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long user1Id;
    private Long user2Id;
    private LocalDateTime createdAt;
}
