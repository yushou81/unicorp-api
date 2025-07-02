package com.csu.unicorp.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 双师课堂创建/更新DTO
 */
@Data
public class DualTeacherCourseDTO {
    
    /**
     * 课程标题
     */
    @NotBlank(message = "课程标题不能为空")
    @Length(min = 2, max = 50, message = "课程标题长度在2-50个字符之间")
    private String title;
    
    /**
     * 课程描述
     */
    @NotBlank(message = "课程描述不能为空")
    @Length(max = 500, message = "课程描述不能超过500个字符")
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
    @NotNull(message = "课程计划时间不能为空")
    @Future(message = "课程计划时间必须是未来时间")
    private LocalDateTime scheduledTime;
    
    /**
     * 最大学生人数
     */
    @Min(value = 1, message = "最大学生人数不能小于1")
    private Integer maxStudents = 50;
    
    /**
     * 课程地点
     */
    private String location;
    
    /**
     * 课程类型：online-在线, offline-线下, hybrid-混合
     */
    @NotBlank(message = "课程类型不能为空")
    private String courseType;
} 