package com.csu.unicorp.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 课程报名DTO
 */
@Data
public class CourseEnrollmentDTO {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Integer courseId;
    
    /**
     * 学生ID，通常从当前登录用户获取，不需要从前端传入
     */
    private Integer studentId;
} 