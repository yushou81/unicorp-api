package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程讨论视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程讨论视图对象")
public class CourseDiscussionVO {
    
    /**
     * 讨论ID
     */
    @Schema(description = "讨论ID", example = "1")
    private Integer id;
    
    /**
     * 课程ID
     */
    @Schema(description = "课程ID", example = "1")
    private Integer courseId;
    
    /**
     * 课程标题
     */
    @Schema(description = "课程标题", example = "Java企业级开发实战")
    private String courseTitle;
    
    /**
     * 发布用户ID
     */
    @Schema(description = "发布用户ID", example = "5")
    private Integer userId;
    
    /**
     * 发布用户姓名
     */
    @Schema(description = "发布用户姓名", example = "张三")
    private String userName;
    
    /**
     * 发布用户头像
     */
    @Schema(description = "发布用户头像", example = "avatars/default/user1.jpg")
    private String userAvatar;
    
    /**
     * 发布用户角色
     */
    @Schema(description = "发布用户角色", example = "STUDENT")
    private String userRole;
    
    /**
     * 讨论内容
     */
    @Schema(description = "讨论内容", example = "这个问题我想了解更多...")
    private String content;
    
    /**
     * 父讨论ID
     */
    @Schema(description = "父讨论ID", example = "5")
    private Integer parentId;
    
    /**
     * 回复列表
     */
    @Schema(description = "回复列表")
    private List<CourseDiscussionVO> replies;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-06-01T10:15:30")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2024-06-01T10:15:30")
    private LocalDateTime updatedAt;
} 