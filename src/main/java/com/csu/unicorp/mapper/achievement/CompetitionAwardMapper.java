package com.csu.unicorp.mapper.achievement;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.achievement.CompetitionAward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 竞赛获奖Mapper接口
 */
@Mapper
public interface CompetitionAwardMapper extends BaseMapper<CompetitionAward> {
    
    /**
     * 根据用户ID查询获奖列表
     * 
     * @param userId 用户ID
     * @return 获奖列表
     */
    @Select("SELECT * FROM competition_awards WHERE user_id = #{userId} AND is_deleted = 0")
    List<CompetitionAward> selectByUserId(@Param("userId") Integer userId);
    
    /**
     * 分页查询用户的获奖列表
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @return 获奖分页列表
     */
    @Select("SELECT * FROM competition_awards WHERE user_id = #{userId} AND is_deleted = 0")
    Page<CompetitionAward> selectPageByUserId(Page<CompetitionAward> page, @Param("userId") Integer userId);
    
    /**
     * 分页查询公开的获奖列表
     * 
     * @param page 分页参数
     * @return 公开获奖分页列表
     */
    @Select("SELECT * FROM competition_awards WHERE is_public = true AND is_deleted = 0")
    Page<CompetitionAward> selectPublicPage(Page<CompetitionAward> page);
    
    /**
     * 分页查询待认证的获奖列表
     * 
     * @param page 分页参数
     * @param organizationId 组织ID
     * @return 待认证获奖分页列表
     */
    @Select("SELECT ca.* FROM competition_awards ca " +
            "JOIN users u ON ca.user_id = u.id " +
            "WHERE u.organization_id = #{organizationId} AND ca.is_verified = false AND ca.is_deleted = 0")
    Page<CompetitionAward> selectUnverifiedPageByOrganization(Page<CompetitionAward> page, @Param("organizationId") Integer organizationId);
    
    /**
     * 根据认证人ID查询已认证的获奖列表
     * 
     * @param page 分页参数
     * @param verifierId 认证人ID
     * @return 已认证获奖分页列表
     */
    @Select("SELECT * FROM competition_awards WHERE verifier_id = #{verifierId} AND is_verified = true AND is_deleted = 0")
    Page<CompetitionAward> selectVerifiedPageByVerifierId(Page<CompetitionAward> page, @Param("verifierId") Integer verifierId);
    
    /**
     * 根据组织ID查询获奖列表
     * 
     * @param page 分页参数
     * @param organizationId 组织ID
     * @return 获奖分页列表
     */
    @Select("SELECT ca.* FROM competition_awards ca " +
            "JOIN users u ON ca.user_id = u.id " +
            "WHERE u.organization_id = #{organizationId} AND ca.is_deleted = 0")
    Page<CompetitionAward> selectByOrganizationId(Page<CompetitionAward> page, @Param("organizationId") Integer organizationId);
} 