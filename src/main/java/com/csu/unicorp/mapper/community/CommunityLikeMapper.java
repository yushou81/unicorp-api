package com.csu.unicorp.mapper.community;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.community.CommunityLike;

/**
 * 社区点赞Mapper接口
 */
public interface CommunityLikeMapper extends BaseMapper<CommunityLike> {
    
    /**
     * 检查用户是否已点赞
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 是否已点赞
     */
    @Select("SELECT COUNT(*) FROM community_like WHERE user_id = #{userId} AND content_type = #{contentType} AND content_id = #{contentId}")
    int checkUserLiked(@Param("userId") Long userId, @Param("contentType") String contentType, @Param("contentId") Long contentId);
    
    /**
     * 获取用户点赞的内容ID列表
     * @param userId 用户ID
     * @param contentType 内容类型
     * @return 内容ID列表
     */
    @Select("SELECT content_id FROM community_like WHERE user_id = #{userId} AND content_type = #{contentType}")
    List<Long> selectUserLikedContentIds(@Param("userId") Long userId, @Param("contentType") String contentType);
    
    /**
     * 删除用户点赞
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 影响行数
     */
    @Delete("DELETE FROM community_like WHERE user_id = #{userId} AND content_type = #{contentType} AND content_id = #{contentId}")
    int deleteUserLike(@Param("userId") Long userId, @Param("contentType") String contentType, @Param("contentId") Long contentId);
    
    /**
     * 获取用户点赞列表
     * @param page 分页参数
     * @param userId 用户ID
     * @param contentType 内容类型
     * @return 点赞列表
     */
    @Select("SELECT * FROM community_like WHERE user_id = #{userId} AND content_type = #{contentType} ORDER BY created_at DESC")
    Page<CommunityLike> selectUserLikes(Page<CommunityLike> page, @Param("userId") Long userId, @Param("contentType") String contentType);
    
    /**
     * 获取内容的点赞数量
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 点赞数量
     */
    @Select("SELECT COUNT(*) FROM community_like WHERE content_type = #{contentType} AND content_id = #{contentId}")
    int countLikesByContent(@Param("contentType") String contentType, @Param("contentId") Long contentId);

    /**
     * 获取内容作者ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 作者ID
     */
    @Select({
        "<script>",
        "SELECT user_id FROM ",
        "<choose>",
        "  <when test=\"contentType == 'TOPIC'\">community_topic</when>",
        "  <when test=\"contentType == 'QUESTION'\">community_question</when>",
        "  <when test=\"contentType == 'ANSWER'\">community_answer</when>",
        "  <when test=\"contentType == 'COMMENT'\">community_comment</when>",
        "  <otherwise>community_topic</otherwise>",
        "</choose>",
        " WHERE id = #{contentId}",
        "</script>"
    })
    Long selectContentAuthorId(@Param("contentType") String contentType, @Param("contentId") Long contentId);
} 