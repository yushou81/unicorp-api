package com.csu.unicorp.dto.achievement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 竞赛获奖创建DTO
 */
@Data
public class CompetitionAwardCreationDTO {
    
    /**
     * 竞赛名称
     */
    @NotBlank(message = "竞赛名称不能为空")
    @Size(max = 100, message = "竞赛名称不能超过100个字符")
    private String competitionName;
    
    /**
     * 获奖等级
     */
    @NotBlank(message = "获奖等级不能为空")
    @Size(max = 50, message = "获奖等级不能超过50个字符")
    private String awardLevel;
    
    /**
     * 获奖日期
     */
    @NotNull(message = "获奖日期不能为空")
    @Past(message = "获奖日期必须是过去的日期")
    private LocalDate awardDate;
    
    /**
     * 主办方
     */
    @Size(max = 100, message = "主办方不能超过100个字符")
    private String organizer;
    
    /**
     * 竞赛描述
     */
    @Size(max = 2000, message = "竞赛描述不能超过2000个字符")
    private String description;
    
    /**
     * 证书图片URL
     */
    @Size(max = 255, message = "证书图片URL不能超过255个字符")
    private String certificateUrl;
    
    /**
     * 团队成员，以逗号分隔
     */
    @Size(max = 255, message = "团队成员不能超过255个字符")
    private String teamMembers;
    
    /**
     * 是否公开
     */
    private Boolean isPublic = true;
}