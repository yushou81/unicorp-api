package com.csu.unicorp.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 设备预约创建DTO
 */
@Data
public class BookingCreationDTO {
    
    @NotNull(message = "设备资源ID不能为空")
    private Integer resourceId;
    
    @NotNull(message = "预约开始时间不能为空")
    @FutureOrPresent(message = "预约开始时间必须是现在或将来的时间")
    private LocalDateTime startTime;
    
    @NotNull(message = "预约结束时间不能为空")
    @FutureOrPresent(message = "预约结束时间必须是现在或将来的时间")
    private LocalDateTime endTime;
    
    @NotBlank(message = "预约目的不能为空")
    @Size(max = 500, message = "预约目的不能超过500个字符")
    private String purpose;
} 