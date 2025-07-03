package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 学习进度视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学习进度视图对象")
public class LearningProgressVO {
    
    /**
     * 进度记录ID
     */
    @Schema(description = "进度记录ID", example = "1")
    private Integer id;
    
    /**
     * 学生ID
     */
    @Schema(description = "学生ID", example = "5")
    private Integer studentId;
    
    /**
     * 学生姓名
     */
    @Schema(description = "学生姓名", example = "张三")
    private String studentName;
    
    /**
     * 章节ID
     */
    @Schema(description = "章节ID", example = "2")
    private Integer chapterId;
    
    /**
     * 章节标题
     */
    @Schema(description = "章节标题", example = "第一章：Java基础")
    private String chapterTitle;
    
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
     * 学习状态
     */
    @Schema(description = "学习状态", example = "completed")
    private String status;
    
    /**
     * 完成百分比(0-100)
     */
    @Schema(description = "完成百分比(0-100)", example = "100")
    private Integer progressPercent;
    
    /**
     * 开始学习时间
     */
    @Schema(description = "开始学习时间", example = "2024-06-20T10:15:30")
    private LocalDateTime startTime;
    
    /**
     * 完成学习时间
     */
    @Schema(description = "完成学习时间", example = "2024-06-20T12:45:30")
    private LocalDateTime completeTime;
    
    /**
     * 学习时长(分钟)
     */
    @Schema(description = "学习时长(分钟)", example = "150")
    private Integer durationMinutes;
    
    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2024-06-20T12:45:30")
    private LocalDateTime updatedAt;
} 