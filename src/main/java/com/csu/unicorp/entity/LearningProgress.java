package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 学习进度实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("learning_progress")
public class LearningProgress {
    
    /**
     * 进度记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 学生ID
     */
    private Integer studentId;
    
    /**
     * 章节ID
     */
    private Integer chapterId;
    
    /**
     * 学习状态：not_started-未开始, in_progress-学习中, completed-已完成
     */
    private String status;
    
    /**
     * 完成百分比(0-100)
     */
    private Integer progressPercent;
    
    /**
     * 开始学习时间
     */
    private LocalDateTime startTime;
    
    /**
     * 完成学习时间
     */
    private LocalDateTime completeTime;
    
    /**
     * 学习时长(分钟)
     */
    private Integer durationMinutes;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 