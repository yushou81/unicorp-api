package com.csu.unicorp.vo;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 设备预约VO
 */
@Data
public class BookingVO {
    
    private Integer id;
    
    private Integer equipmentId;
    
    private String equipmentName;
    
    private Integer userId;
    
    private String userName;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private String purpose;
    
    private String status;
    
    private String rejectReason;
    
    private Integer reviewerId;
    
    private String reviewerName;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 