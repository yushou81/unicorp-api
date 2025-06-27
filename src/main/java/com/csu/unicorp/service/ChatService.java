package com.csu.unicorp.service;

import java.util.List;

import com.csu.unicorp.entity.ChatMessage;
import com.csu.unicorp.entity.ChatSession;

public interface ChatService {
    ChatSession createOrGetSession(Long user1Id, Long user2Id);
    ChatMessage sendMessage(Long sessionId, Long senderId, String content);
    List<ChatMessage> getMessages(Long sessionId);
    List<ChatSession> getUserSessions(Long userId);
}
