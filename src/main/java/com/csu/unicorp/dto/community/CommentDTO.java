package com.csu.unicorp.dto.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 社区评论DTO
 */
@Data
@Schema(description = "社区评论DTO")
public class CommentDTO {
    
    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Schema(description = "评论内容", required = true, example = "这是一条评论")
    private String content;
    
    /**
     * 所属话题ID
     */
    @NotNull(message = "话题ID不能为空")
    @Schema(description = "所属话题ID", required = true, example = "1")
    private Long topicId;
    
    /**
     * 父评论ID（回复评论时使用）
     */
    @Schema(description = "父评论ID（回复评论时使用）", example = "1")
    private Long parentId;
} 