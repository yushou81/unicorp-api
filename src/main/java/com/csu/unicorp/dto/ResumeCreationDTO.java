package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 简历创建DTO
 */
@Data
@Schema(description = "简历创建DTO")
public class ResumeCreationDTO {

    /**
     * 专业
     */
    @NotBlank(message = "专业不能为空")
    @Schema(description = "专业", required = true)
    private String major;
    
    /**
     * 教育水平
     */
    @NotBlank(message = "教育水平不能为空")
    @Schema(description = "教育水平", required = true)
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