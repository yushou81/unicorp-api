package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.csu.unicorp.entity.ChatMessage;
import com.csu.unicorp.entity.ChatSession;
import com.csu.unicorp.mapper.ChatMessageMapper;
import com.csu.unicorp.mapper.ChatSessionMapper;
import com.csu.unicorp.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Override
    public ChatSession createOrGetSession(Long user1Id, Long user2Id) {
        // 保证 user1Id < user2Id，避免重复会话
        if (user1Id > user2Id) {
            Long temp = user1Id;
            user1Id = user2Id;
            user2Id = temp;
        }
        QueryWrapper<ChatSession> wrapper = new QueryWrapper<>();
        wrapper.eq("user1_id", user1Id).eq("user2_id", user2Id);
        ChatSession session = chatSessionMapper.selectOne(wrapper);
        if (session == null) {
            session = new ChatSession();
            session.setUser1Id(user1Id);
            session.setUser2Id(user2Id);
            session.setCreatedAt(LocalDateTime.now());
            chatSessionMapper.insert(session);
        }
        return session;
    }

    @Override
    public ChatMessage sendMessage(Long sessionId, Long senderId, String content) {
        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());
        message.setIsRead(false);
        chatMessageMapper.insert(message);
        return message;
    }

    @Override
    public List<ChatMessage> getMessages(Long sessionId) {
        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("session_id", sessionId).orderByAsc("sent_at");
        return chatMessageMapper.selectList(wrapper);
    }

    @Override
    public List<ChatSession> getUserSessions(Long userId) {
        QueryWrapper<ChatSession> wrapper = new QueryWrapper<>();
        wrapper.and(w -> w.eq("user1_id", userId).or().eq("user2_id", userId));
        return chatSessionMapper.selectList(wrapper);
    }
}
