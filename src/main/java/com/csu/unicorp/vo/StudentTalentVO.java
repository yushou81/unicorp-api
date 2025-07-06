package com.csu.unicorp.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学生人才VO类
 * 用于返回推荐的学生人才信息
 */
@Data
public class StudentTalentVO {
    
    /**
     * 推荐ID
     */
    private Integer id;
    
    /**
     * 组织ID
     */
    private Integer organizationId;
    
    /**
     * 学生ID
     */
    private Integer studentId;
    
    /**
     * 推荐分数
     */
    private Double score;
    
    /**
     * 推荐原因
     */
    private String reason;
    
    /**
     * 推荐状态
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
     * 用户ID（与学生ID相同）
     */
    private Integer userId;
    
    /**
     * 账号
     */
    private String account;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 电话
     */
    private String phone;
    
    /**
     * 专业
     */
    private String major;
    
    /**
     * 学历
     */
    private String educationLevel;
    
    /**
     * 毕业年份
     */
    private Integer graduationYear;
    
    /**
     * 简历列表
     */
    private java.util.List<ResumeVO> resumes;
    
    /**
     * 成就概览
     */
    private Object achievementOverview;
} 