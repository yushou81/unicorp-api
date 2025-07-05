package com.csu.unicorp.dto.community;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 社区问题DTO
 */
@Data
@Schema(description = "社区问题DTO")
public class QuestionDTO {
    
    /**
     * 问题标题
     */
    @NotBlank(message = "问题标题不能为空")
    @Size(max = 200, message = "问题标题不能超过200个字符")
    @Schema(description = "问题标题", required = true, example = "如何解决Spring Boot中的循环依赖问题？")
    private String title;
    
    /**
     * 问题描述
     */
    @NotBlank(message = "问题描述不能为空")
    @Schema(description = "问题描述", required = true, example = "我在Spring Boot项目中遇到了循环依赖问题，具体表现为...")
    private String content;
    
    /**
     * 所属分类ID
     */
    @NotNull(message = "分类ID不能为空")
    @Schema(description = "所属分类ID", required = true, example = "1")
    private Long categoryId;
    
    /**
     * 标签ID列表
     */
    @Schema(description = "标签ID列表", example = "[1, 2, 3]")
    private List<Long> tagIds;
    
    /**
     * 悬赏积分
     */
    @Schema(description = "悬赏积分", example = "10")
    private Integer bountyPoints;
} 