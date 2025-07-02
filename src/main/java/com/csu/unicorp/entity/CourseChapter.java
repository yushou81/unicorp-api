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
 * 课程章节实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("course_chapters")
public class CourseChapter {
    
    /**
     * 章节ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 关联的双师课堂ID
     */
    private Integer courseId;
    
    /**
     * 章节标题
     */
    private String title;
    
    /**
     * 章节描述
     */
    private String description;
    
    /**
     * 章节顺序
     */
    private Integer sequence;
    
    /**
     * 是否已发布
     */
    private Boolean isPublished;
    
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