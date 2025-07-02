package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程评价实体类
 */
@Data
@TableName("course_ratings")
public class CourseRating {
    
    /**
     * 评价ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 课程ID
     */
    private Integer courseId;
    
    /**
     * 学生ID
     */
    private Integer studentId;
    
    /**
     * 评分(1-5)
     */
    private Integer rating;
    
    /**
     * 评价内容
     */
    private String comment;
    
    /**
     * 是否匿名
     */
    private Boolean isAnonymous;
    
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