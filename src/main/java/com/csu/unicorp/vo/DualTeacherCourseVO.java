package com.csu.unicorp.vo;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 双师课堂视图对象
 */
@Data
@Builder
public class DualTeacherCourseVO {
    
    /**
     * 课程ID
     */
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
     * 教师姓名
     */
    private String teacherName;
    
    /**
     * 企业导师ID
     */
    private Integer mentorId;
    
    /**
     * 企业导师姓名
     */
    private String mentorName;
    
    /**
     * 企业名称
     */
    private String enterpriseName;
    
    /**
     * 课程计划时间
     */
    private LocalDateTime scheduledTime;
    
    /**
     * 最大学生人数
     */
    private Integer maxStudents;
    
    /**
     * 已报名学生人数
     */
    private Integer enrolledCount;
    
    /**
     * 课程地点
     */
    private String location;
    
    /**
     * 课程类型：online-在线, offline-线下, hybrid-混合
     */
    private String courseType;
    
    /**
     * 课程状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 