package com.csu.unicorp.vo.community;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社区回答视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "社区回答视图对象")
public class AnswerVO {
    
    /**
     * 回答ID
     */
    @Schema(description = "回答ID", example = "1")
    private Long id;
    
    /**
     * 回答内容
     */
    @Schema(description = "回答内容", example = "解决Spring Boot循环依赖问题的方法有以下几种...")
    private String content;
    
    /**
     * 回答用户ID
     */
    @Schema(description = "回答用户ID", example = "1")
    private Long userId;
    
    /**
     * 回答用户昵称
     */
    @Schema(description = "回答用户昵称", example = "技术大牛")
    private String userName;
    
    /**
     * 回答用户头像
     */
    @Schema(description = "回答用户头像", example = "/avatars/user2.jpg")
    private String userAvatar;
    
    /**
     * 所属问题ID
     */
    @Schema(description = "所属问题ID", example = "1")
    private Long questionId;
    
    /**
     * 点赞数量
     */
    @Schema(description = "点赞数量", example = "20")
    private Integer likeCount;
    
    /**
     * 是否被采纳
     */
    @Schema(description = "是否被采纳", example = "false")
    private Boolean isAccepted;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2023-01-01 12:00:00")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2023-01-01 12:00:00")
    private LocalDateTime updatedAt;
    
    /**
     * 当前用户是否已点赞
     */
    @Schema(description = "当前用户是否已点赞", example = "false")
    private Boolean liked;
} 