package com.csu.unicorp.mapper.community;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.community.CommunityFavorite;

/**
 * 社区收藏Mapper接口
 */
public interface CommunityFavoriteMapper extends BaseMapper<CommunityFavorite> {
    
    /**
     * 检查用户是否已收藏
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 是否已收藏
     */
    @Select("SELECT COUNT(*) FROM community_favorite WHERE user_id = #{userId} AND content_type = #{contentType} AND content_id = #{contentId}")
    int checkUserFavorited(@Param("userId") Long userId, @Param("contentType") String contentType, @Param("contentId") Long contentId);
    
    /**
     * 获取用户收藏的内容ID列表
     * @param userId 用户ID
     * @param contentType 内容类型
     * @return 内容ID列表
     */
    @Select("SELECT content_id FROM community_favorite WHERE user_id = #{userId} AND content_type = #{contentType}")
    List<Long> selectUserFavoritedContentIds(@Param("userId") Long userId, @Param("contentType") String contentType);
    
    /**
     * 删除用户收藏
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 影响行数
     */
    @Delete("DELETE FROM community_favorite WHERE user_id = #{userId} AND content_type = #{contentType} AND content_id = #{contentId}")
    int deleteUserFavorite(@Param("userId") Long userId, @Param("contentType") String contentType, @Param("contentId") Long contentId);
    
    /**
     * 获取用户收藏列表
     * @param page 分页参数
     * @param userId 用户ID
     * @param contentType 内容类型
     * @return 收藏列表
     */
    @Select("SELECT * FROM community_favorite WHERE user_id = #{userId} AND content_type = #{contentType} ORDER BY created_at DESC")
    Page<CommunityFavorite> selectUserFavorites(Page<CommunityFavorite> page, @Param("userId") Long userId, @Param("contentType") String contentType);
    
    /**
     * 获取内容的收藏数量
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 收藏数量
     */
    @Select("SELECT COUNT(*) FROM community_favorite WHERE content_type = #{contentType} AND content_id = #{contentId}")
    int countFavoritesByContent(@Param("contentType") String contentType, @Param("contentId") Long contentId);
} 