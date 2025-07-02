package com.csu.unicorp.entity.course;

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
 * 课程讨论实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("course_discussions")
public class CourseDiscussion {
    
    /**
     * 讨论ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 课程ID
     */
    private Integer courseId;
    
    /**
     * 发布用户ID
     */
    private Integer userId;
    
    /**
     * 讨论内容
     */
    private String content;
    
    /**
     * 父讨论ID，用于回复
     */
    private Integer parentId;
    
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