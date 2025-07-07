package com.csu.unicorp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 审计日志视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogVO {
    /**
     * 日志ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 用户账号
     */
    private String userAccount;
    
    /**
     * 用户名称
     */
    private String userName;
    
    /**
     * 操作类型
     */
    private String action;
    
    /**
     * 操作类型描述
     */
    private String actionDesc;
    
    /**
     * 所属模块
     */
    private String module;
    
    /**
     * 操作详情
     */
    private String details;
    
    /**
     * 操作结果
     */
    private String result;
    
    /**
     * IP地址
     */
    private String ip;
    
    /**
     * 操作时间
     */
    private LocalDateTime timestamp;
} 