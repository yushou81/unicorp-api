package com.csu.unicorp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 岗位申请状态更新DTO
 */
@Data
@Schema(description = "岗位申请状态更新数据")
public class ApplicationStatusUpdateDTO {
    
    /**
     * 申请状态：viewed-已查看，interviewing-面试中，offered-已录用，rejected-已拒绝
     */
    @NotBlank(message = "申请状态不能为空")
    @Pattern(regexp = "viewed|interviewing|offered|rejected", message = "状态值无效，必须是：viewed, interviewing, offered, rejected 之一")
    @Schema(description = "申请状态", example = "viewed", allowableValues = {"viewed", "interviewing", "offered", "rejected"})
    private String status;
} 