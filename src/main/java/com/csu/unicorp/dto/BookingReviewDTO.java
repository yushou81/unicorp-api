package com.csu.unicorp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 预约审核DTO
 */
@Data
public class BookingReviewDTO {
    
    @NotNull(message = "预约ID不能为空")
    private Integer bookingId;
    
    @NotBlank(message = "审核结果不能为空")
    private String approve; // APPROVED 或 REJECTED
    
    @Size(max = 500, message = "拒绝原因不能超过500个字符")
    private String rejectReason; // 当approve为REJECTED时必填
} 