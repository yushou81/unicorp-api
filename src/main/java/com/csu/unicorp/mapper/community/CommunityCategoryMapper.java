package com.csu.unicorp.mapper.community;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.community.CommunityCategory;

/**
 * 社区板块Mapper接口
 * 
 * @author unicorp
 */
@Mapper
public interface CommunityCategoryMapper extends BaseMapper<CommunityCategory> {
    
    /**
     * 获取所有顶级板块
     * @return 顶级板块列表
     */
    @Select("SELECT * FROM community_category WHERE parent_id IS NULL ORDER BY sort_order ASC")
    List<CommunityCategory> selectRootCategories();
    
    /**
     * 获取指定板块的子板块
     * @param parentId 父板块ID
     * @return 子板块列表
     */
    @Select("SELECT * FROM community_category WHERE parent_id = #{parentId} ORDER BY sort_order ASC")
    List<CommunityCategory> selectChildCategories(@Param("parentId") Long parentId);
    
    /**
     * 获取指定板块的话题数量
     * @param categoryId 板块ID
     * @return 话题数量
     */
    @Select("SELECT COUNT(*) FROM community_topic WHERE category_id = #{categoryId} AND status = 'NORMAL'")
    Integer countTopicsByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 获取所有板块（包含话题数量）
     * @param page 分页参数
     * @return 板块列表
     */
    @Select("SELECT c.*, (SELECT COUNT(*) FROM community_topic t WHERE t.category_id = c.id AND t.status = 'NORMAL') AS topic_count " +
            "FROM community_category c ORDER BY c.sort_order ASC")
    Page<CommunityCategory> selectCategoriesWithTopicCount(Page<CommunityCategory> page);
    
    /**
     * 更新板块排序
     * @param categoryId 板块ID
     * @param sortOrder 排序顺序
     * @return 影响行数
     */
    @Update("UPDATE community_category SET sort_order = #{sortOrder} WHERE id = #{categoryId}")
    int updateSortOrder(@Param("categoryId") Long categoryId, @Param("sortOrder") Integer sortOrder);
} 