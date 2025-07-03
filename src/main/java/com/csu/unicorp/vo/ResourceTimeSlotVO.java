package com.csu.unicorp.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源占用时间段VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceTimeSlotVO {
    
    /**
     * 预约ID
     */
    private Integer bookingId;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 预约状态 (PENDING-待审核, APPROVED-已批准, REJECTED-已拒绝, CANCELED-已取消, COMPLETED-已完成)
     */
    private String status;
    
    /**
     * 是否是当前用户的预约
     */
    private boolean isCurrentUser;
    
    /**
     * 是否为有效占用时间段（只有APPROVED状态才是真正占用时间段）
     */
    private boolean isOccupied;
} 