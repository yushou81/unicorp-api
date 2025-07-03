package com.csu.unicorp.mapper.achievement;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.achievement.ResearchAchievement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 科研成果Mapper接口
 */
@Mapper
public interface ResearchAchievementMapper extends BaseMapper<ResearchAchievement> {
    
    /**
     * 根据用户ID查询科研成果列表
     * 
     * @param userId 用户ID
     * @return 科研成果列表
     */
    @Select("SELECT * FROM research_achievements WHERE user_id = #{userId} AND is_deleted = 0")
    List<ResearchAchievement> selectByUserId(@Param("userId") Integer userId);
    
    /**
     * 分页查询用户的科研成果列表
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @return 科研成果分页列表
     */
    @Select("SELECT * FROM research_achievements WHERE user_id = #{userId} AND is_deleted = 0")
    Page<ResearchAchievement> selectPageByUserId(Page<ResearchAchievement> page, @Param("userId") Integer userId);
    
    /**
     * 分页查询公开的科研成果列表
     * 
     * @param page 分页参数
     * @return 公开科研成果分页列表
     */
    @Select("SELECT * FROM research_achievements WHERE is_public = true AND is_deleted = 0")
    Page<ResearchAchievement> selectPublicPage(Page<ResearchAchievement> page);
    
    /**
     * 根据类型分页查询公开的科研成果列表
     * 
     * @param page 分页参数
     * @param type 成果类型
     * @return 公开科研成果分页列表
     */
    @Select("SELECT * FROM research_achievements WHERE type = #{type} AND is_public = true AND is_deleted = 0")
    Page<ResearchAchievement> selectPublicPageByType(Page<ResearchAchievement> page, @Param("type") String type);
    
    /**
     * 分页查询待认证的科研成果列表
     * 
     * @param page 分页参数
     * @param organizationId 组织ID
     * @return 待认证科研成果分页列表
     */
    @Select("SELECT ra.* FROM research_achievements ra " +
            "JOIN users u ON ra.user_id = u.id " +
            "WHERE u.organization_id = #{organizationId} AND ra.is_verified = false AND ra.is_deleted = 0")
    Page<ResearchAchievement> selectUnverifiedPageByOrganization(Page<ResearchAchievement> page, @Param("organizationId") Integer organizationId);
    
    /**
     * 根据认证人ID查询已认证的科研成果列表
     * 
     * @param page 分页参数
     * @param verifierId 认证人ID
     * @return 已认证科研成果分页列表
     */
    @Select("SELECT * FROM research_achievements WHERE verifier_id = #{verifierId} AND is_verified = true AND is_deleted = 0")
    Page<ResearchAchievement> selectVerifiedPageByVerifierId(Page<ResearchAchievement> page, @Param("verifierId") Integer verifierId);
} 