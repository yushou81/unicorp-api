package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统日志实体类，对应audit_logs表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("audit_logs")
public class AuditLog {
    /**
     * 日志ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 操作类型
     */
    private String action;
    
    /**
     * 操作时间
     */
    private LocalDateTime timestamp;
    
    /**
     * 操作详情
     */
    private String details;
} 