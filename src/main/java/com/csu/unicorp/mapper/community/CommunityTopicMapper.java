package com.csu.unicorp.mapper.community;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.community.CommunityTopic;

/**
 * 社区话题Mapper接口
 */
public interface CommunityTopicMapper extends BaseMapper<CommunityTopic> {
    
    /**
     * 获取指定板块的话题列表
     * @param page 分页参数
     * @param categoryId 板块ID
     * @return 话题列表
     */
    @Select("SELECT t.* FROM community_topic t " +
            "WHERE t.category_id = #{categoryId} AND t.status = 'NORMAL' " +
            "ORDER BY t.is_sticky DESC, t.is_essence DESC, t.created_at DESC")
    Page<CommunityTopic> selectTopicsByCategoryId(Page<CommunityTopic> page, @Param("categoryId") Long categoryId);
    
    /**
     * 获取热门话题列表
     * @param page 分页参数
     * @return 热门话题列表
     */
    @Select("SELECT t.* FROM community_topic t " +
            "WHERE t.status = 'NORMAL' " +
            "ORDER BY t.view_count DESC, t.comment_count DESC, t.like_count DESC, t.created_at DESC")
    Page<CommunityTopic> selectHotTopics(Page<CommunityTopic> page);
    
    /**
     * 获取最新话题列表
     * @param page 分页参数
     * @return 最新话题列表
     */
    @Select("SELECT t.* FROM community_topic t " +
            "WHERE t.status = 'NORMAL' " +
            "ORDER BY t.created_at DESC")
    Page<CommunityTopic> selectLatestTopics(Page<CommunityTopic> page);
    
    /**
     * 获取精华话题列表
     * @param page 分页参数
     * @return 精华话题列表
     */
    @Select("SELECT t.* FROM community_topic t " +
            "WHERE t.status = 'NORMAL' AND t.is_essence = 1 " +
            "ORDER BY t.created_at DESC")
    Page<CommunityTopic> selectEssenceTopics(Page<CommunityTopic> page);
    
    /**
     * 获取用户发布的话题列表
     * @param page 分页参数
     * @param userId 用户ID
     * @return 用户话题列表
     */
    @Select("SELECT t.* FROM community_topic t " +
            "WHERE t.user_id = #{userId} AND t.status != 'DELETED' " +
            "ORDER BY t.created_at DESC")
    Page<CommunityTopic> selectTopicsByUserId(Page<CommunityTopic> page, @Param("userId") Long userId);
    
    /**
     * 增加话题浏览次数
     * @param topicId 话题ID
     * @return 影响行数
     */
    @Update("UPDATE community_topic SET view_count = view_count + 1 WHERE id = #{topicId}")
    int incrementViewCount(@Param("topicId") Long topicId);
    
    /**
     * 增加话题评论数量
     * @param topicId 话题ID
     * @return 影响行数
     */
    @Update("UPDATE community_topic SET comment_count = comment_count + 1 WHERE id = #{topicId}")
    int incrementCommentCount(@Param("topicId") Long topicId);
    
    /**
     * 减少话题评论数量
     * @param topicId 话题ID
     * @return 影响行数
     */
    @Update("UPDATE community_topic SET comment_count = GREATEST(0, comment_count - 1) WHERE id = #{topicId}")
    int decrementCommentCount(@Param("topicId") Long topicId);
    
    /**
     * 增加话题点赞数量
     * @param topicId 话题ID
     * @return 影响行数
     */
    @Update("UPDATE community_topic SET like_count = like_count + 1 WHERE id = #{topicId}")
    int incrementLikeCount(@Param("topicId") Long topicId);
    
    /**
     * 减少话题点赞数量
     * @param topicId 话题ID
     * @return 影响行数
     */
    @Update("UPDATE community_topic SET like_count = GREATEST(0, like_count - 1) WHERE id = #{topicId}")
    int decrementLikeCount(@Param("topicId") Long topicId);
    
    /**
     * 设置话题置顶状态
     * @param topicId 话题ID
     * @param isSticky 是否置顶
     * @return 影响行数
     */
    @Update("UPDATE community_topic SET is_sticky = #{isSticky} WHERE id = #{topicId}")
    int updateStickyStatus(@Param("topicId") Long topicId, @Param("isSticky") Boolean isSticky);
    
    /**
     * 设置话题精华状态
     * @param topicId 话题ID
     * @param isEssence 是否精华
     * @return 影响行数
     */
    @Update("UPDATE community_topic SET is_essence = #{isEssence} WHERE id = #{topicId}")
    int updateEssenceStatus(@Param("topicId") Long topicId, @Param("isEssence") Boolean isEssence);
    
    /**
     * 搜索话题
     * @param page 分页参数
     * @param keyword 关键词
     * @return 话题列表
     */
    @Select("SELECT t.* FROM community_topic t " +
            "WHERE t.status = 'NORMAL' AND (t.title LIKE CONCAT('%', #{keyword}, '%') OR t.content LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY t.is_sticky DESC, t.is_essence DESC, t.created_at DESC")
    Page<CommunityTopic> searchTopics(Page<CommunityTopic> page, @Param("keyword") String keyword);
} 