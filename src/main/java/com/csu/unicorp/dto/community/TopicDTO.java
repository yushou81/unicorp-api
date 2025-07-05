package com.csu.unicorp.dto.community;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 社区话题DTO
 */
@Data
@Schema(description = "社区话题DTO")
public class TopicDTO {
    
    /**
     * 话题标题
     */
    @NotBlank(message = "话题标题不能为空")
    @Size(max = 200, message = "话题标题不能超过200个字符")
    @Schema(description = "话题标题", required = true, example = "Spring Boot 3.0新特性分享")
    private String title;
    
    /**
     * 话题内容
     */
    @NotBlank(message = "话题内容不能为空")
    @Schema(description = "话题内容", required = true, example = "Spring Boot 3.0发布了，带来了很多新特性...")
    private String content;
    
    /**
     * 所属板块ID
     */
    @NotNull(message = "板块ID不能为空")
    @Schema(description = "所属板块ID", required = true, example = "1")
    private Long categoryId;
    
    /**
     * 标签ID列表
     */
    @Schema(description = "标签ID列表", example = "[1, 2, 3]")
    private List<Long> tagIds;
} 