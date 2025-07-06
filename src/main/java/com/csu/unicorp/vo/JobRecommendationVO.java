package com.csu.unicorp.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 岗位推荐VO类
 * 用于返回推荐的岗位信息
 */
@Data
public class JobRecommendationVO {
    
    /**
     * 推荐ID
     */
    private Integer id;
    
    /**
     * 岗位ID
     */
    private Integer jobId;
    
    /**
     * 岗位标题
     */
    private String title;
    
    /**
     * 组织名称
     */
    private String organizationName;
    
    /**
     * 工作地点
     */
    private String location;
    
    /**
     * 工作类型
     */
    private String jobType;
    
    /**
     * 最低薪资
     */
    private Integer salaryMin;
    
    /**
     * 最高薪资
     */
    private Integer salaryMax;
    
    /**
     * 薪资单位
     */
    private String salaryUnit;
    
    /**
     * 岗位分类ID
     */
    private Integer categoryId;
    
    /**
     * 岗位分类
     */
    private JobCategoryVO category;
    
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
     * 岗位标签
     */
    private String tags;
} 