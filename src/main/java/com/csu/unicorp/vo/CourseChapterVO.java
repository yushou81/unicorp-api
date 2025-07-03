package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程章节视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程章节视图对象")
public class CourseChapterVO {
    
    /**
     * 章节ID
     */
    @Schema(description = "章节ID", example = "1")
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
     * 章节标题
     */
    @Schema(description = "章节标题", example = "第一章：Java基础")
    private String title;
    
    /**
     * 章节描述
     */
    @Schema(description = "章节描述", example = "本章介绍Java语言基础知识，包括变量、数据类型、运算符等")
    private String description;
    
    /**
     * 章节顺序
     */
    @Schema(description = "章节顺序", example = "1")
    private Integer sequence;
    
    /**
     * 是否已发布
     */
    @Schema(description = "是否已发布", example = "true")
    private Boolean isPublished;
    
    /**
     * 关联的资源列表
     */
    @Schema(description = "关联的资源列表")
    private List<CourseResourceVO> resources;
    
    /**
     * 学生完成数量
     */
    @Schema(description = "学生完成数量", example = "15")
    private Integer completedCount;
    
    /**
     * 总学生数量
     */
    @Schema(description = "总学生数量", example = "30")
    private Integer totalStudentCount;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-06-01T10:15:30")
    private LocalDateTime createdAt;
} 