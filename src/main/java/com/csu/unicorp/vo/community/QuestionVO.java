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
 * 社区问题视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "社区问题视图对象")
public class QuestionVO {
    
    /**
     * 问题ID
     */
    @Schema(description = "问题ID", example = "1")
    private Long id;
    
    /**
     * 问题标题
     */
    @Schema(description = "问题标题", example = "如何解决Spring Boot中的循环依赖问题？")
    private String title;
    
    /**
     * 问题描述
     */
    @Schema(description = "问题描述", example = "我在Spring Boot项目中遇到了循环依赖问题，具体表现为...")
    private String content;
    
    /**
     * 提问用户ID
     */
    @Schema(description = "提问用户ID", example = "1")
    private Long userId;
    
    /**
     * 提问用户昵称
     */
    @Schema(description = "提问用户昵称", example = "技术小达人")
    private String userName;
    
    /**
     * 提问用户头像
     */
    @Schema(description = "提问用户头像", example = "/avatars/user1.jpg")
    private String userAvatar;
    
    /**
     * 所属分类ID
     */
    @Schema(description = "所属分类ID", example = "1")
    private Long categoryId;
    
    /**
     * 所属分类名称
     */
    @Schema(description = "所属分类名称", example = "技术交流")
    private String categoryName;
    
    /**
     * 浏览次数
     */
    @Schema(description = "浏览次数", example = "100")
    private Integer viewCount;
    
    /**
     * 回答数量
     */
    @Schema(description = "回答数量", example = "10")
    private Integer answerCount;
    
    /**
     * 悬赏积分
     */
    @Schema(description = "悬赏积分", example = "10")
    private Integer bountyPoints;
    
    /**
     * 最佳答案ID
     */
    @Schema(description = "最佳答案ID", example = "1")
    private Long bestAnswerId;
    
    /**
     * 最佳答案内容
     */
    @Schema(description = "最佳答案内容")
    private AnswerVO bestAnswer;
    
    /**
     * 状态：UNSOLVED-未解决，SOLVED-已解决，CLOSED-已关闭
     */
    @Schema(description = "状态：UNSOLVED-未解决，SOLVED-已解决，CLOSED-已关闭", example = "UNSOLVED")
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
     * 当前用户是否已收藏
     */
    @Schema(description = "当前用户是否已收藏", example = "false")
    private Boolean favorited;
} 