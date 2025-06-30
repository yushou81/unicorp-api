package com.csu.unicorp.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备使用申请实体类，对应equipment_applications表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("equipment_applications")
public class EquipmentApplication {
    
    /**
     * 申请ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 设备资源ID
     */
    private Integer resourceId;
    
    /**
     * 申请用户ID
     */
    private Integer userId;
    
    /**
     * 申请开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 申请结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 使用目的
     */
    private String purpose;
    
    /**
     * 申请状态：pending(待审核), approved(已批准), rejected(已拒绝)
     */
    private String status;
    
    /**
     * 审核意见
     */
    private String reviewComment;
    
    /**
     * 审核人ID
     */
    private Integer reviewedByUserId;
    
    /**
     * 审核时间
     */
    private LocalDateTime reviewedAt;
    
    /**
     * 逻辑删除标志
     */
    @TableLogic
    private Boolean isDeleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 