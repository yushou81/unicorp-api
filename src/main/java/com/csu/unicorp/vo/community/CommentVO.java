package com.csu.unicorp.vo.community;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社区评论视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "社区评论视图对象")
public class CommentVO {
    
    /**
     * 评论ID
     */
    @Schema(description = "评论ID", example = "1")
    private Long id;
    
    /**
     * 评论内容
     */
    @Schema(description = "评论内容", example = "这是一条评论")
    private String content;
    
    /**
     * 评论用户ID
     */
    @Schema(description = "评论用户ID", example = "1")
    private Long userId;
    
    /**
     * 评论用户昵称
     */
    @Schema(description = "评论用户昵称", example = "技术小达人")
    private String userName;
    
    /**
     * 评论用户头像
     */
    @Schema(description = "评论用户头像", example = "/avatars/user1.jpg")
    private String userAvatar;
    
    /**
     * 所属话题ID
     */
    @Schema(description = "所属话题ID", example = "1")
    private Long topicId;
    
    /**
     * 父评论ID
     */
    @Schema(description = "父评论ID", example = "1")
    private Long parentId;
    
    /**
     * 父评论用户昵称
     */
    @Schema(description = "父评论用户昵称", example = "技术小达人")
    private String parentUserName;
    
    /**
     * 点赞数量
     */
    @Schema(description = "点赞数量", example = "10")
    private Integer likeCount;
    
    /**
     * 状态：NORMAL-正常，DELETED-已删除
     */
    @Schema(description = "状态：NORMAL-正常，DELETED-已删除", example = "NORMAL")
    private String status;
    
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
    
    /**
     * 回复列表
     */
    @Schema(description = "回复列表")
    private List<CommentVO> replies;
} 