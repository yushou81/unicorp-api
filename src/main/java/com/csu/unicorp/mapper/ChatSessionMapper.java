package com.csu.unicorp.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.ChatSession;

/**
 * 聊天会话Mapper接口
 */
@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {
    
    /**
     * 根据两个用户ID查找会话
     * @param user1Id 用户1 ID
     * @param user2Id 用户2 ID
     * @return 聊天会话
     */
    @Select("SELECT * FROM chat_session WHERE (user1_id = #{user1Id} AND user2_id = #{user2Id}) OR (user1_id = #{user2Id} AND user2_id = #{user1Id}) LIMIT 1")
    ChatSession findByUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
} 