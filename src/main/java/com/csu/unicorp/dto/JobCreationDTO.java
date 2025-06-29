package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 岗位创建DTO
 */
@Data
@Schema(description = "岗位创建DTO")
public class JobCreationDTO {
    
    /**
     * 岗位标题
     */
    @NotBlank(message = "岗位标题不能为空")
    @Schema(description = "岗位标题", required = true)
    private String title;
    
    /**
     * 岗位描述
     */
    @NotBlank(message = "岗位描述不能为空")
    @Schema(description = "岗位描述", required = true)
    private String description;
    
    /**
     * 工作地点
     */
    @Schema(description = "工作地点")
    private String location;
    
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
    @NotNull(message = "工作类型不能为空")
    @Schema(description = "工作类型", required = true, allowableValues = {"full_time", "internship", "part_time", "remote"})
    private String jobType;
    
    /**
     * 招聘人数
     */
    @Schema(description = "招聘人数", defaultValue = "1")
    private Integer headcount = 1;
    
    /**
     * 学历要求
     */
    @Schema(description = "学历要求", allowableValues = {"bachelor", "master", "doctorate", "any"}, defaultValue = "any")
    private String educationRequirement = "any";
    
    /**
     * 经验要求
     */
    @Schema(description = "经验要求", allowableValues = {"fresh_graduate", "less_than_1_year", "1_to_3_years", "any"}, defaultValue = "any")
    private String experienceRequirement = "any";
    
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
} 