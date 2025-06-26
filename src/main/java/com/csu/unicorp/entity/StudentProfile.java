package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 学生档案实体类，对应student_profiles表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("student_profiles")
public class StudentProfile {
    /**
     * 档案ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 学生全名
     */
    private String fullName;
    
    /**
     * 专业
     */
    private String major;
    
    /**
     * 教育水平
     */
    private String educationLevel;
    
    /**
     * 简历URL
     */
    private String resumeUrl;
    
    /**
     * 成就和荣誉
     */
    private String achievements;
} 