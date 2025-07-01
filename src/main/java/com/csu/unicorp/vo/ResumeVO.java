package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 简历VO
 */
@Data
@Schema(description = "简历VO")
public class ResumeVO {
    
    /**
     * 简历ID
     */
    @Schema(description = "简历ID")
    private Integer id;
    
    /**
     * 专业
     */
    @Schema(description = "专业")
    private String major;
    
    /**
     * 教育水平
     */
    @Schema(description = "教育水平")
    private String educationLevel;
    
    /**
     * 简历URL
     */
    @Schema(description = "简历URL")
    private String resumeUrl;
    
    /**
     * 成就和荣誉
     */
    @Schema(description = "成就和荣誉")
    private String achievements;
} 