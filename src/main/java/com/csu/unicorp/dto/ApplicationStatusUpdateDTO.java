package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 岗位申请状态更新DTO
 */
@Data
@Schema(description = "岗位申请状态更新DTO")
public class ApplicationStatusUpdateDTO {
    
    /**
     * 申请状态
     */
    @NotBlank(message = "申请状态不能为空")
    @Schema(description = "申请状态", required = true, allowableValues = {"viewed", "interviewing", "offered", "rejected"})
    private String status;
} 