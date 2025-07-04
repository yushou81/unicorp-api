package com.csu.unicorp.mapper.community;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.community.CommunityComment;

/**
 * 社区评论Mapper接口
 */
public interface CommunityCommentMapper extends BaseMapper<CommunityComment> {
    
    /**
     * 获取话题的评论列表（不包含回复）
     * @param page 分页参数
     * @param topicId 话题ID
     * @return 评论列表
     */
    @Select("SELECT c.* FROM community_comment c " +
            "WHERE c.topic_id = #{topicId} AND c.parent_id IS NULL AND c.status = 'NORMAL' " +
            "ORDER BY c.created_at DESC")
    Page<CommunityComment> selectCommentsByTopicId(Page<CommunityComment> page, @Param("topicId") Long topicId);
    
    /**
     * 获取评论的回复列表
     * @param commentId 评论ID
     * @return 回复列表
     */
    @Select("SELECT c.* FROM community_comment c " +
            "WHERE c.parent_id = #{commentId} AND c.status = 'NORMAL' " +
            "ORDER BY c.created_at ASC")
    List<CommunityComment> selectRepliesByCommentId(@Param("commentId") Long commentId);
    
    /**
     * 获取用户的评论列表
     * @param page 分页参数
     * @param userId 用户ID
     * @return 评论列表
     */
    @Select("SELECT c.* FROM community_comment c " +
            "WHERE c.user_id = #{userId} AND c.status = 'NORMAL' " +
            "ORDER BY c.created_at DESC")
    Page<CommunityComment> selectCommentsByUserId(Page<CommunityComment> page, @Param("userId") Long userId);
    
    /**
     * 增加评论点赞数量
     * @param commentId 评论ID
     * @return 影响行数
     */
    @Update("UPDATE community_comment SET like_count = like_count + 1 WHERE id = #{commentId}")
    int incrementLikeCount(@Param("commentId") Long commentId);
    
    /**
     * 减少评论点赞数量
     * @param commentId 评论ID
     * @return 影响行数
     */
    @Update("UPDATE community_comment SET like_count = GREATEST(0, like_count - 1) WHERE id = #{commentId}")
    int decrementLikeCount(@Param("commentId") Long commentId);
    
    /**
     * 获取话题的所有评论数量（包含回复）
     * @param topicId 话题ID
     * @return 评论数量
     */
    @Select("SELECT COUNT(*) FROM community_comment WHERE topic_id = #{topicId} AND status = 'NORMAL'")
    Integer countCommentsByTopicId(@Param("topicId") Long topicId);
    
    /**
     * 获取评论的回复数量
     * @param commentId 评论ID
     * @return 回复数量
     */
    @Select("SELECT COUNT(*) FROM community_comment WHERE parent_id = #{commentId} AND status = 'NORMAL'")
    Integer countRepliesByCommentId(@Param("commentId") Long commentId);

    /**
     * 获取内容作者ID
     * @param contentId 内容ID
     * @return 作者ID
     */
    @Select("SELECT t.user_id FROM community_topic t WHERE t.id = #{contentId} " +
            "UNION ALL " +
            "SELECT a.user_id FROM community_answer a WHERE a.id = #{contentId} " +
            "LIMIT 1")
    Long selectContentAuthorId(@Param("contentId") Long contentId);
} 