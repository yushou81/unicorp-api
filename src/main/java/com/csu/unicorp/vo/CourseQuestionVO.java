package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 课程问答视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程问答视图对象")
public class CourseQuestionVO {
    
    /**
     * 问题ID
     */
    @Schema(description = "问题ID", example = "1")
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
     * 相关章节ID
     */
    @Schema(description = "相关章节ID", example = "2")
    private Integer chapterId;
    
    /**
     * 章节标题
     */
    @Schema(description = "章节标题", example = "第一章：Java基础")
    private String chapterTitle;
    
    /**
     * 提问学生ID
     */
    @Schema(description = "提问学生ID", example = "5")
    private Integer studentId;
    
    /**
     * 提问学生姓名
     */
    @Schema(description = "提问学生姓名", example = "张三")
    private String studentName;
    
    /**
     * 问题标题
     */
    @Schema(description = "问题标题", example = "Java中的多态是如何实现的？")
    private String title;
    
    /**
     * 问题内容
     */
    @Schema(description = "问题内容", example = "我对Java中的多态概念有些疑惑，特别是关于接口和抽象类的使用...")
    private String content;
    
    /**
     * 问题状态
     */
    @Schema(description = "问题状态", example = "answered")
    private String status;
    
    /**
     * 回答内容
     */
    @Schema(description = "回答内容", example = "多态是指同一个行为具有多个不同表现形式或形态的能力...")
    private String answer;
    
    /**
     * 回答者ID
     */
    @Schema(description = "回答者ID", example = "10")
    private Integer answeredBy;
    
    /**
     * 回答者姓名
     */
    @Schema(description = "回答者姓名", example = "李四")
    private String answeredByName;
    
    /**
     * 回答者角色
     */
    @Schema(description = "回答者角色", example = "TEACHER")
    private String answeredByRole;
    
    /**
     * 回答时间
     */
    @Schema(description = "回答时间", example = "2024-06-15T10:15:30")
    private LocalDateTime answeredAt;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-06-10T10:15:30")
    private LocalDateTime createdAt;
} 