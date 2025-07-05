package com.csu.unicorp.mapper.community;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.community.CommunityContentTag;

/**
 * 内容标签关联Mapper接口
 */
public interface CommunityContentTagMapper extends BaseMapper<CommunityContentTag> {
    
    /**
     * 批量插入内容标签关联
     * @param contentTagList 内容标签关联列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<CommunityContentTag> contentTagList);
    
    /**
     * 删除内容的所有标签关联
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 影响行数
     */
    @Delete("DELETE FROM community_content_tag WHERE content_type = #{contentType} AND content_id = #{contentId}")
    int deleteByContent(@Param("contentType") String contentType, @Param("contentId") Long contentId);
    
    /**
     * 获取标签关联的内容ID列表
     * @param contentType 内容类型
     * @param tagId 标签ID
     * @return 内容ID列表
     */
    @Select("SELECT content_id FROM community_content_tag WHERE content_type = #{contentType} AND tag_id = #{tagId}")
    List<Long> selectContentIdsByTagId(@Param("contentType") String contentType, @Param("tagId") Long tagId);
    
    /**
     * 检查内容是否包含指定标签
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @param tagId 标签ID
     * @return 是否包含
     */
    @Select("SELECT COUNT(*) FROM community_content_tag WHERE content_type = #{contentType} AND content_id = #{contentId} AND tag_id = #{tagId}")
    int checkContentHasTag(@Param("contentType") String contentType, @Param("contentId") Long contentId, @Param("tagId") Long tagId);
} 