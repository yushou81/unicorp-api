package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户状态更新DTO
 */
@Data
@Schema(description = "用户状态更新DTO")
public class UserStatusUpdateDTO {
    
    /**
     * 用户状态
     */
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "^(active|inactive|pending_approval)$", message = "状态只能是active、inactive或pending_approval")
    @Schema(description = "用户状态：active-活跃，inactive-不活跃，pending_approval-等待审批", example = "active")
    private String status;
} 