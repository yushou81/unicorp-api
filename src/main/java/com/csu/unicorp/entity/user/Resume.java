package com.csu.unicorp.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 简历实体类，对应resumes表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("resumes")
public class Resume {
    /**
     * 简历ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
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