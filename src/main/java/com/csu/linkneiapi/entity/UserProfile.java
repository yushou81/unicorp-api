package com.csu.linkneiapi.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户简历/档案实体类
 */
@Data
@TableName("user_profile")
public class UserProfile {

    /**
     * 关联的用户ID，同时也是主键
     */
    @TableId
    private Long userId;
    
    /**
     * 真实姓名
     */
    private String fullName;
    
    /**
     * 最高学历, e.g., 本科, 硕士
     */
    private String educationLevel;
    
    /**
     * 毕业院校
     */
    private String university;
    
    /**
     * 所学专业
     */
    private String major;
    
    /**
     * 毕业年份
     */
    private Integer graduationYear;
    
    /**
     * 期望职位
     */
    private String targetJobTitle;
    
    /**
     * 期望薪资范围
     */
    private String targetSalaryRange;
    
    /**
     * 简历附件URL (相对路径)
     */
    private String resumeUrl;
    
    /**
     * 自我介绍
     */
    private String selfIntroduction;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 关联的用户信息（非数据库字段）
     */
    @TableField(exist = false)
    private User user;
} 