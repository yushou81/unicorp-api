package com.csu.unicorp.entity.course;

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
 * 双师课堂实体类，对应dual_teacher_courses表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("dual_teacher_courses")
public class DualTeacherCourse {
    /**
     * 课程ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 课程标题
     */
    private String title;
    
    /**
     * 课程描述
     */
    private String description;
    
    /**
     * 教师ID
     */
    private Integer teacherId;
    
    /**
     * 企业导师ID
     */
    private Integer mentorId;
    
    /**
     * 课程计划时间
     */
    private LocalDateTime scheduledTime;
    
    /**
     * 最大学生人数
     */
    private Integer maxStudents;
    
    /**
     * 课程地点
     */
    private String location;
    
    /**
     * 课程类型：online-在线, offline-线下, hybrid-混合
     */
    private String courseType;
    
    /**
     * 课程状态：planning-筹备中, open-开放报名, in_progress-进行中, completed-已结束, cancelled-已取消
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Boolean isDeleted;
}