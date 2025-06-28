package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 简历更新DTO
 */
@Data
@Schema(description = "简历更新请求")
public class ResumeUpdateDTO {
    
    /**
     * 简历URL
     */
    @NotBlank(message = "简历URL不能为空")
    @Pattern(regexp = "^https?://.*$", message = "简历URL格式不正确")
    @Schema(description = "简历文件URL", example = "http://example.com/resumes/resume123.pdf")
    private String resumeUrl;
} 