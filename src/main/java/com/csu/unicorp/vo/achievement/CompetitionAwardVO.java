package com.csu.unicorp.vo.achievement;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 竞赛获奖VO
 */
@Data
public class CompetitionAwardVO {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 所属学生ID
     */
    private Integer userId;
    
    /**
     * 学生姓名
     */
    private String userName;
    
    /**
     * 学生所属组织
     */
    private String organizationName;
    
    /**
     * 竞赛名称
     */
    private String competitionName;
    
    /**
     * 获奖等级
     */
    private String awardLevel;
    
    /**
     * 获奖日期
     */
    private LocalDate awardDate;
    
    /**
     * 主办方
     */
    private String organizer;
    
    /**
     * 竞赛描述
     */
    private String description;
    
    /**
     * 证书图片URL
     */
    private String certificateUrl;
    
    /**
     * 团队成员，以逗号分隔
     */
    private String teamMembers;
    
    /**
     * 是否已认证
     */
    private Boolean isVerified;
    
    /**
     * 认证人ID
     */
    private Integer verifierId;
    
    /**
     * 认证人姓名
     */
    private String verifierName;
    
    /**
     * 认证日期
     */
    private LocalDateTime verifyDate;
    
    /**
     * 是否公开
     */
    private Boolean isPublic;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 