package com.csu.unicorp.dto.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 社区回答DTO
 */
@Data
@Schema(description = "社区回答DTO")
public class AnswerDTO {
    
    /**
     * 回答内容
     */
    @NotBlank(message = "回答内容不能为空")
    @Schema(description = "回答内容", required = true, example = "解决Spring Boot循环依赖问题的方法有以下几种...")
    private String content;
    
    /**
     * 所属问题ID
     */
    @NotNull(message = "问题ID不能为空")
    @Schema(description = "所属问题ID", required = true, example = "1")
    private Long questionId;
} 