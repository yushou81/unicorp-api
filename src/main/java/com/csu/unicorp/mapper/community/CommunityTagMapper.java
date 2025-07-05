package com.csu.unicorp.mapper.community;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.community.CommunityTag;

/**
 * 社区标签Mapper接口
 */
public interface CommunityTagMapper extends BaseMapper<CommunityTag> {
    
    /**
     * 获取热门标签列表
     * @param limit 限制数量
     * @return 热门标签列表
     */
    @Select("SELECT * FROM community_tag ORDER BY usage_count DESC LIMIT #{limit}")
    List<CommunityTag> selectHotTags(@Param("limit") Integer limit);
    
    /**
     * 获取所有标签
     * @param page 分页参数
     * @return 标签列表
     */
    @Select("SELECT * FROM community_tag ORDER BY usage_count DESC")
    Page<CommunityTag> selectAllTags(Page<CommunityTag> page);
    
    /**
     * 根据内容ID和类型获取标签列表
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 标签列表
     */
    @Select("SELECT t.* FROM community_tag t " +
            "INNER JOIN community_content_tag ct ON t.id = ct.tag_id " +
            "WHERE ct.content_type = #{contentType} AND ct.content_id = #{contentId}")
    List<CommunityTag> selectTagsByContent(@Param("contentType") String contentType, @Param("contentId") Long contentId);
    
    /**
     * 增加标签使用次数
     * @param tagId 标签ID
     * @return 影响行数
     */
    @Update("UPDATE community_tag SET usage_count = usage_count + 1 WHERE id = #{tagId}")
    int incrementUsageCount(@Param("tagId") Long tagId);
    
    /**
     * 减少标签使用次数
     * @param tagId 标签ID
     * @return 影响行数
     */
    @Update("UPDATE community_tag SET usage_count = GREATEST(0, usage_count - 1) WHERE id = #{tagId}")
    int decrementUsageCount(@Param("tagId") Long tagId);
    
    /**
     * 搜索标签
     * @param keyword 关键词
     * @return 标签列表
     */
    @Select("SELECT * FROM community_tag WHERE name LIKE CONCAT('%', #{keyword}, '%') ORDER BY usage_count DESC")
    List<CommunityTag> searchTags(@Param("keyword") String keyword);
} 