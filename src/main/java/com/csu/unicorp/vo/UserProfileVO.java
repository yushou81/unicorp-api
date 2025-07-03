package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户个人主页VO
 */
@Data
@Schema(description = "用户个人主页VO")
public class UserProfileVO {
    
    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer id;
    
    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    private String account;
    
    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickname;
    
    /**
     * 头像URL
     */
    @Schema(description = "头像URL")
    private String avatarUrl;
    
    /**
     * 个人简介
     */
    @Schema(description = "个人简介")
    private String bio;
    
    /**
     * 所属组织名称
     */
    @Schema(description = "所属组织名称")
    private String organizationName;
    
    /**
     * 用户角色列表
     */
    @Schema(description = "用户角色列表")
    private String role;
    
    /**
     * 简历信息（仅学生角色有此字段）
     */
    @Schema(description = "简历信息（仅学生角色有此字段）")
    private ResumeVO resume;
    
    /**
     * 作品集列表（仅学生角色有此字段）
     */
    @Schema(description = "作品集列表（仅学生角色有此字段）")
    private List<PortfolioItemVO> portfolio;
} 