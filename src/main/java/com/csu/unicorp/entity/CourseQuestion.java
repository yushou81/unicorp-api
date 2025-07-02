package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 课程问答实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("course_questions")
public class CourseQuestion {
    
    /**
     * 问题ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 课程ID
     */
    private Integer courseId;
    
    /**
     * 相关章节ID
     */
    private Integer chapterId;
    
    /**
     * 提问学生ID
     */
    private Integer studentId;
    
    /**
     * 问题标题
     */
    private String title;
    
    /**
     * 问题内容
     */
    private String content;
    
    /**
     * 问题状态：pending-待回答, answered-已回答, closed-已关闭
     */
    private String status;
    
    /**
     * 回答内容
     */
    private String answer;
    
    /**
     * 回答者ID
     */
    private Integer answeredBy;
    
    /**
     * 回答时间
     */
    private LocalDateTime answeredAt;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否删除
     */
    @TableLogic
    private Boolean isDeleted;
} 