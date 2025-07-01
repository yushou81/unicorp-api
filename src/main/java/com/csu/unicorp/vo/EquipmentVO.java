package com.csu.unicorp.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 实验设备VO
 */
@Data
public class EquipmentVO {
    
    private Integer id;
    
    private String name;
    
    private String description;
    
    private String imageUrl;
    
    private String location;
    
    private String status;
    
    private Integer managerId;
    
    private String managerName;
    
    private Integer organizationId;
    
    private String organizationName;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 