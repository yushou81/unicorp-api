package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 双师课堂选课记录实体类，对应course_enrollments表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("course_enrollments")
public class CourseEnrollment {
    /**
     * 选课记录ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 关联的双师课堂ID
     */
    private Integer courseId;
    
    /**
     * 学生ID
     */
    private Integer studentId;
    
    /**
     * 选课状态：enrolled-已报名, cancelled-已取消, completed-已完成
     */
    private String status;
    
    /**
     * 报名时间
     */
    private LocalDateTime enrollmentTime;
    
    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Boolean isDeleted;
} 