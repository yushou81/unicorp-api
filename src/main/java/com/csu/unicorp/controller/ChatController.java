package com.csu.unicorp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.csu.unicorp.entity.ChatMessage;
import com.csu.unicorp.entity.ChatSession;
import com.csu.unicorp.service.ChatService;

@RestController
@RequestMapping("/v1/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // 创建或获取会话
    @PostMapping("/session")
    public ChatSession createOrGetSession(@RequestParam Long user1Id, @RequestParam Long user2Id) {
        return chatService.createOrGetSession(user1Id, user2Id);
    }

    // 发送消息
    @PostMapping("/message")
    public ChatMessage sendMessage(@RequestParam Long sessionId, @RequestParam Long senderId, @RequestParam String content) {
        return chatService.sendMessage(sessionId, senderId, content);
    }

    // 获取会话消息
    @GetMapping("/messages")
    public List<ChatMessage> getMessages(@RequestParam Long sessionId) {
        return chatService.getMessages(sessionId);
    }

    // 获取用户所有会话
    @GetMapping("/sessions")
    public List<ChatSession> getUserSessions(@RequestParam Long userId) {
        return chatService.getUserSessions(userId);
    }
}
