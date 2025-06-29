package com.csu.unicorp.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.ChatMessage;

/**
 * 聊天消息Mapper接口
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    
    /**
     * 获取会话的最近消息
     * @param sessionId 会话ID
     * @param limit 消息数量限制
     * @return 消息列表
     */
    @Select("SELECT * FROM chat_message WHERE session_id = #{sessionId} ORDER BY sent_at DESC LIMIT #{limit}")
    List<ChatMessage> findRecentMessages(@Param("sessionId") Long sessionId, @Param("limit") Integer limit);
    
    /**
     * 统计会话中的未读消息数
     * @param sessionId 会话ID
     * @param receiverId 接收者ID
     * @return 未读消息数
     */
    @Select("SELECT COUNT(*) FROM chat_message WHERE session_id = #{sessionId} AND sender_id != #{receiverId} AND is_read = 0")
    Integer countUnreadMessages(@Param("sessionId") Long sessionId, @Param("receiverId") Long receiverId);
    
    /**
     * 标记会话中用户的所有消息为已读
     * @param sessionId 会话ID
     * @param receiverId 接收者ID
     */
    @Update("UPDATE chat_message SET is_read = 1 WHERE session_id = #{sessionId}  AND is_read = 0")
    void markAllAsRead(@Param("sessionId") Long sessionId, @Param("receiverId") Long receiverId);
} 