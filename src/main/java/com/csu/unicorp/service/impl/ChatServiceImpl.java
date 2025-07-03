package com.csu.unicorp.service.impl;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.csu.unicorp.service.FileService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.dto.ChatMessageDTO;
import com.csu.unicorp.entity.ChatMessage;
import com.csu.unicorp.entity.ChatSession;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.ChatMessageMapper;
import com.csu.unicorp.mapper.ChatSessionMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.ChatService;
import com.csu.unicorp.vo.ChatMessageVO;
import com.csu.unicorp.vo.ChatSessionVO;

import lombok.RequiredArgsConstructor;

/**
 * 聊天服务实现类
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    
    private final ChatSessionMapper chatSessionMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final UserMapper userMapper;
    private final FileService fileService;
    
    @Override
    public List<ChatSessionVO> getUserSessions(Long userId) {
        // 查询用户参与的所有会话
        LambdaQueryWrapper<ChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatSession::getUser1Id, userId).or().eq(ChatSession::getUser2Id, userId);
        List<ChatSession> sessions = chatSessionMapper.selectList(wrapper);
        
        if (sessions.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 获取所有会话中的用户ID（排除当前用户）
        List<Integer> otherUserIds = sessions.stream()
                .map(session -> {
                    Long otherUserId = session.getUser1Id().equals(userId) ? session.getUser2Id() : session.getUser1Id();
                    return otherUserId.intValue(); // 转换为Integer类型
                })
                .collect(Collectors.toList());
        
        // 批量查询用户信息
        List<User> users = userMapper.selectBatchIds(otherUserIds);
        Map<Integer, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        
        // 构建会话VO
        List<ChatSessionVO> result = new ArrayList<>(sessions.size());
        for (ChatSession session : sessions) {
            ChatSessionVO vo = new ChatSessionVO();
            vo.setId(session.getId());
            
            // 设置对话用户信息
            Long otherUserId = session.getUser1Id().equals(userId) ? session.getUser2Id() : session.getUser1Id();
            User otherUser = userMap.get(otherUserId.intValue());
            if (otherUser != null) {
                vo.setUserId(otherUserId);
                vo.setUserName(otherUser.getNickname());
                String avatar = fileService.getFullFileUrl(otherUser.getAvatar());
                vo.setUserAvatar(avatar);
            }
            
            // 获取最近一条消息
            List<ChatMessage> recentMessages = chatMessageMapper.findRecentMessages(session.getId(), 1);
            if (!recentMessages.isEmpty()) {
                ChatMessage lastMessage = recentMessages.get(0);
                vo.setLastMessage(lastMessage.getContent());
                vo.setLastMessageTime(lastMessage.getSentAt());
            } else {
                vo.setLastMessageTime(session.getCreatedAt());
            }
            
            // 获取未读消息数
            Integer unreadCount = chatMessageMapper.countUnreadMessages(session.getId(), userId);
            vo.setUnreadCount(unreadCount);
            
            result.add(vo);
        }
        
        // 按最后消息时间倒序排列
        result.sort((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()));
        
        return result;
    }
    
    
    @Override
    public ChatSessionVO getSessionDetail(Long sessionId, Long userId) {
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null) {
            return null;
        }
        
        // 验证当前用户是否为会话成员
        if (!session.getUser1Id().equals(userId) && !session.getUser2Id().equals(userId)) {
            return null;
        }
        
        // 获取对话用户信息
        Long otherUserId = session.getUser1Id().equals(userId) ? session.getUser2Id() : session.getUser1Id();
        User otherUser = userMapper.selectById(otherUserId.intValue());
        
        ChatSessionVO vo = new ChatSessionVO();
        vo.setId(session.getId());
        
        if (otherUser != null) {
            vo.setUserId(otherUserId);
            vo.setUserName(otherUser.getNickname());
            vo.setUserAvatar(otherUser.getAvatar());
        }
        
        // 获取最近一条消息
        List<ChatMessage> recentMessages = chatMessageMapper.findRecentMessages(session.getId(), 1);
        if (!recentMessages.isEmpty()) {
            ChatMessage lastMessage = recentMessages.get(0);
            vo.setLastMessage(lastMessage.getContent());
            vo.setLastMessageTime(lastMessage.getSentAt());
        } else {
            vo.setLastMessageTime(session.getCreatedAt());
        }
        
        // 获取未读消息数
        Integer unreadCount = chatMessageMapper.countUnreadMessages(session.getId(), userId);
        vo.setUnreadCount(unreadCount);
        
        return vo;
    }
    
    @Override
    @Transactional
    public ChatSession getOrCreateSession(Long user1Id, Long user2Id) {
        // 查找现有会话
        ChatSession session = chatSessionMapper.findByUsers(user1Id, user2Id);
        
        // 如果不存在，则创建新会话
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
    public ChatMessage saveMessage(ChatMessage message) {
        if (message.getSentAt() == null) {
            message.setSentAt(LocalDateTime.now());
        }
        if (message.getIsRead() == null) {
            message.setIsRead(0);
        }
        chatMessageMapper.insert(message);
        return message;
    }
    
    @Override
    @Transactional
    public ChatMessageVO sendMessage(ChatMessageDTO messageDTO) {
        // 获取或创建会话
        ChatSession session = getOrCreateSession(messageDTO.getSenderId(), messageDTO.getReceiverId());
        
        // 创建并保存消息
        ChatMessage message = new ChatMessage();
        message.setSessionId(session.getId());
        message.setSenderId(messageDTO.getSenderId());
        message.setContent(messageDTO.getContent());
        message.setSentAt(LocalDateTime.now());
        message.setIsRead(0);
        
        saveMessage(message);
        
        // 转换为VO返回
        return convertToMessageVO(message);
    }
    
    @Override
    public List<ChatMessageVO> getSessionMessages(Long sessionId, Long currentUserId, Integer page, Integer size) {
        // 默认值处理
        page = page == null || page < 1 ? 1 : page;
        size = size == null || size < 1 ? 20 : size;
        
        // 获取会话信息，确定对话另一方的ID
        ChatSession session = chatSessionMapper.selectById(sessionId);
        if (session == null) {
            return Collections.emptyList();
        }
        
        // 获取接收方ID（对话的另一方）
        Long receiverId = session.getUser1Id().equals(currentUserId) ? session.getUser2Id() : session.getUser1Id();
        
        // 分页查询消息
        IPage<ChatMessage> messagePage = new Page<>(page, size);
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getSessionId, sessionId)
                .orderByDesc(ChatMessage::getSentAt);
        
        IPage<ChatMessage> result = chatMessageMapper.selectPage(messagePage, wrapper);
        
        if (result.getRecords().isEmpty()) {
            return Collections.emptyList();
        }
        
        // 获取发送者信息
        List<Integer> senderIds = result.getRecords().stream()
                .map(message -> message.getSenderId().intValue())
                .distinct()
                .collect(Collectors.toList());
        
        List<User> users = userMapper.selectBatchIds(senderIds);
        Map<Integer, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
        
        // 转换为VO列表
        return result.getRecords().stream()
                .map(message -> {
                    ChatMessageVO vo = new ChatMessageVO();
                    BeanUtils.copyProperties(message, vo);
                    
                    User sender = userMap.get(message.getSenderId().intValue());
                    if (sender != null) {
                        vo.setSenderName(sender.getNickname());
                    }
                    
                    // 设置接收方ID
                    vo.setReceiverId(receiverId);
                    
                    return vo;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void markMessagesAsRead(Long sessionId, Long userId) {
        chatMessageMapper.markAllAsRead(sessionId, userId);
    }
    
    /**
     * 将消息实体转换为视图对象
     * @param message 消息实体
     * @return 消息视图对象
     */
    private ChatMessageVO convertToMessageVO(ChatMessage message) {
        ChatMessageVO vo = new ChatMessageVO();
        BeanUtils.copyProperties(message, vo);
        
        // 设置发送者名称
        User sender = userMapper.selectById(message.getSenderId().intValue());
        if (sender != null) {
            vo.setSenderName(sender.getNickname());
        }
        
        // 获取会话信息以确定接收方ID
        ChatSession session = chatSessionMapper.selectById(message.getSessionId());
        if (session != null) {
            // 接收方是会话中发送者之外的另一个用户
            Long receiverId = session.getUser1Id().equals(message.getSenderId()) ? 
                session.getUser2Id() : session.getUser1Id();
            vo.setReceiverId(receiverId);
        }
        
        vo.setType(ChatMessageDTO.MessageType.CHAT);
        
        return vo;
    }
} 