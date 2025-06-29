package com.csu.unicorp.service;

import java.util.List;

import com.csu.unicorp.dto.ChatMessageDTO;
import com.csu.unicorp.entity.ChatMessage;
import com.csu.unicorp.entity.ChatSession;
import com.csu.unicorp.vo.ChatMessageVO;
import com.csu.unicorp.vo.ChatSessionVO;

/**
 * 聊天服务接口
 */
public interface ChatService {
    
    /**
     * 获取用户的所有聊天会话
     * @param userId 用户ID
     * @return 聊天会话列表
     */
    List<ChatSessionVO> getUserSessions(Long userId);
    
    /**
     * 获取会话详情
     * @param sessionId 会话ID
     * @param userId 当前用户ID
     * @return 会话详情
     */
    ChatSessionVO getSessionDetail(Long sessionId, Long userId);
    
    /**
     * 获取或创建两个用户之间的会话
     * @param user1Id 用户1 ID
     * @param user2Id 用户2 ID
     * @return 会话对象
     */
    ChatSession getOrCreateSession(Long user1Id, Long user2Id);
    
    /**
     * 保存聊天消息
     * @param message 聊天消息
     * @return 保存后的聊天消息
     */
    ChatMessage saveMessage(ChatMessage message);
    
    /**
     * 发送聊天消息
     * @param messageDTO 消息数据
     * @return 聊天消息视图对象
     */
    ChatMessageVO sendMessage(ChatMessageDTO messageDTO);
    
    /**
     * 获取会话的历史消息
     * @param sessionId 会话ID
     * @param page 页码
     * @param size 每页大小
     * @return 历史消息列表
     */
    List<ChatMessageVO> getSessionMessages(Long sessionId, Integer page, Integer size);
    
    /**
     * 将会话中的消息标记为已读
     * @param sessionId 会话ID
     * @param userId 用户ID
     */
    void markMessagesAsRead(Long sessionId, Long userId);
} 