package com.csu.unicorp.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 实验设备预约实体
 */
@Data
@TableName("equipment_bookings")
public class EquipmentBooking {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 关联的设备ID
     */
    private Integer equipmentId;
    
    /**
     * 预约用户ID
     */
    private Integer userId;
    
    /**
     * 预约开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 预约结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 预约目的
     */
    private String purpose;
    
    /**
     * 状态：PENDING-待审核, APPROVED-已批准, REJECTED-已拒绝, CANCELED-已取消, COMPLETED-已完成
     */
    private String status;
    
    /**
     * 拒绝原因（如果被拒绝）
     */
    private String rejectReason;
    
    /**
     * 审核人ID
     */
    private Integer reviewerId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 