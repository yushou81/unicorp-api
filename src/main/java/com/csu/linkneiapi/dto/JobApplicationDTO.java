package com.csu.linkneiapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 投递岗位DTO
 */
@Data
@Schema(description = "岗位投递请求数据")
public class JobApplicationDTO {

    /**
     * 投递岗位ID
     */
    @NotNull(message = "岗位ID不能为空")
    @Schema(description = "投递岗位ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long jobPostId;
    
    /**
     * 投递附言(可选)
     */
    @Schema(description = "投递附言", maxLength = 500)
    private String coverLetter;
} 