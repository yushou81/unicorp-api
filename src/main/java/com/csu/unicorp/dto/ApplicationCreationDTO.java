package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * 岗位申请创建DTO
 */
@Data
@Schema(description = "岗位申请创建DTO")
public class ApplicationCreationDTO {
    
    /**
     * 岗位ID
     */
    @NotNull(message = "岗位ID不能为空")
    @Schema(description = "岗位ID", required = true)
    private Integer jobId;
    
    /**
     * 简历ID
     */
    @NotNull(message = "简历ID不能为空")
    @Schema(description = "简历ID（关联学生档案ID）", required = true)
    private Integer resumeId;
} 