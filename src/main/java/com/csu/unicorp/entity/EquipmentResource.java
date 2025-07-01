package com.csu.unicorp.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;

import lombok.Data;

/**
 * 实验设备资源实体
 */
@Data
@TableName("equipment_resources")
public class EquipmentResource {
    
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 设备名称
     */
    private String name;
    
    /**
     * 设备描述
     */
    private String description;
    
    /**
     * 设备照片URL
     */
    private String imageUrl;
    
    /**
     * 设备位置
     */
    private String location;
    
    /**
     * 设备状态：AVAILABLE-可用, MAINTENANCE-维护中, RESERVED-已预约
     */
    private String status;
    
    /**
     * 设备管理员ID
     */
    private Integer managerId;
    
    /**
     * 所属组织ID
     */
    private Integer organizationId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 乐观锁版本
     */
    @Version
    private Integer version;
} 