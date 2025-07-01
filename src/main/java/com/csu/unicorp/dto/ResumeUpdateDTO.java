package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 简历更新DTO
 */
@Data
@Schema(description = "简历更新DTO")
public class ResumeUpdateDTO {
    
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
    @Schema(description = "简历URL", example = "http://example.com/resumes/resume123.pdf")
    private String resumeUrl;
    
    /**
     * 成就和荣誉
     */
    @Schema(description = "成就和荣誉")
    private String achievements;
} 