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
 * 社区话题视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "社区话题视图对象")
public class TopicVO {
    
    /**
     * 话题ID
     */
    @Schema(description = "话题ID", example = "1")
    private Long id;
    
    /**
     * 话题标题
     */
    @Schema(description = "话题标题", example = "Spring Boot 3.0新特性分享")
    private String title;
    
    /**
     * 话题内容
     */
    @Schema(description = "话题内容", example = "Spring Boot 3.0发布了，带来了很多新特性...")
    private String content;
    
    /**
     * 发布用户ID
     */
    @Schema(description = "发布用户ID", example = "1")
    private Long userId;
    
    /**
     * 发布用户昵称
     */
    @Schema(description = "发布用户昵称", example = "技术小达人")
    private String userName;
    
    /**
     * 发布用户头像
     */
    @Schema(description = "发布用户头像", example = "/avatars/user1.jpg")
    private String userAvatar;
    
    /**
     * 所属板块ID
     */
    @Schema(description = "所属板块ID", example = "1")
    private Long categoryId;
    
    /**
     * 所属板块名称
     */
    @Schema(description = "所属板块名称", example = "技术交流")
    private String categoryName;
    
    /**
     * 浏览次数
     */
    @Schema(description = "浏览次数", example = "100")
    private Integer viewCount;
    
    /**
     * 评论数量
     */
    @Schema(description = "评论数量", example = "10")
    private Integer commentCount;
    
    /**
     * 点赞数量
     */
    @Schema(description = "点赞数量", example = "20")
    private Integer likeCount;
    
    /**
     * 是否置顶
     */
    @Schema(description = "是否置顶", example = "false")
    private Boolean isSticky;
    
    /**
     * 是否精华
     */
    @Schema(description = "是否精华", example = "false")
    private Boolean isEssence;
    
    /**
     * 状态：NORMAL-正常，PENDING-待审核，DELETED-已删除
     */
    @Schema(description = "状态：NORMAL-正常，PENDING-待审核，DELETED-已删除", example = "NORMAL")
    private String status;
    
    /**
     * 标签列表
     */
    @Schema(description = "标签列表")
    private List<TagVO> tags;
    
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
     * 当前用户是否已收藏
     */
    @Schema(description = "当前用户是否已收藏", example = "false")
    private Boolean favorited;
} 