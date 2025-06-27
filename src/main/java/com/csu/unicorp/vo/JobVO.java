package com.csu.unicorp.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 岗位VO
 */
@Data
public class JobVO {
    /**
     * 岗位ID
     */
    private Integer id;
    
    /**
     * 发布组织ID
     */
    private Integer organizationId;
    
    /**
     * 发布组织名称
     */
    private String organizationName;
    
    /**
     * 发布用户ID
     */
    private Integer postedByUserId;
    
    /**
     * 岗位标题
     */
    private String title;
    
    /**
     * 岗位描述
     */
    private String description;
    
    /**
     * 工作地点
     */
    private String location;
    
    /**
     * 岗位状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 