package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 岗位信息VO
 */
@Data
@Schema(description = "岗位信息VO")
public class JobVO {
    
    /**
     * 岗位ID
     */
    @Schema(description = "岗位ID")
    private Integer id;
    
    /**
     * 发布组织ID
     */
    @Schema(description = "发布组织ID")
    private Integer organizationId;
    
    /**
     * 组织名称
     */
    @Schema(description = "组织名称")
    private String organizationName;
    
    /**
     * 发布用户ID
     */
    @Schema(description = "发布用户ID")
    private Integer postedByUserId;
    
    /**
     * 岗位标题
     */
    @Schema(description = "岗位标题")
    private String title;
    
    /**
     * 岗位描述
     */
    @Schema(description = "岗位描述")
    private String description;
    
    /**
     * 工作地点
     */
    @Schema(description = "工作地点")
    private String location;
    
    /**
     * 岗位状态
     */
    @Schema(description = "岗位状态")
    private String status;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
    
    /**
     * 最低薪资 (单位: k)
     */
    @Schema(description = "最低薪资 (单位: k)")
    private Integer salaryMin;
    
    /**
     * 最高薪资 (单位: k)
     */
    @Schema(description = "最高薪资 (单位: k)")
    private Integer salaryMax;
    
    /**
     * 薪资单位 (月/年)
     */
    @Schema(description = "薪资单位", allowableValues = {"per_month", "per_year"})
    private String salaryUnit;
    
    /**
     * 工作类型
     */
    @Schema(description = "工作类型", allowableValues = {"full_time", "internship", "part_time", "remote"})
    private String jobType;
    
    /**
     * 招聘人数
     */
    @Schema(description = "招聘人数")
    private Integer headcount;
    
    /**
     * 学历要求
     */
    @Schema(description = "学历要求", allowableValues = {"bachelor", "master", "doctorate", "any"})
    private String educationRequirement;
    
    /**
     * 经验要求
     */
    @Schema(description = "经验要求", allowableValues = {"fresh_graduate", "less_than_1_year", "1_to_3_years", "any"})
    private String experienceRequirement;
    
    /**
     * 职能分类
     */
    @Schema(description = "职能分类")
    private String jobCategory;
    
    /**
     * 技能标签 (以逗号分隔)
     */
    @Schema(description = "技能标签 (以逗号分隔)")
    private String skillTags;
    
    /**
     * 申请截止日期
     */
    @Schema(description = "申请截止日期")
    private LocalDate applicationDeadline;
    
    /**
     * 浏览量
     */
    @Schema(description = "浏览量")
    private Integer viewCount;
} 