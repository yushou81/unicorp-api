package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 岗位申请详情VO
 */
@Data
@Schema(description = "岗位申请详情VO")
public class ApplicationDetailVO {
    /**
     * 申请ID
     */
    @Schema(description = "申请ID")
    private Integer id;
    
    /**
     * 岗位ID
     */
    @Schema(description = "岗位ID")
    private Integer jobId;
    
    /**
     * 学生ID
     */
    @Schema(description = "学生ID")
    private Integer studentId;
    
    /**
     * 简历ID
     */
    @Schema(description = "简历ID")
    private Integer resumeId;
    
    /**
     * 申请状态
     */
    @Schema(description = "申请状态")
    private String status;
    
    /**
     * 申请时间
     */
    @Schema(description = "申请时间")
    private LocalDateTime appliedAt;
    
    /**
     * 简历信息
     */
    @Schema(description = "简历信息")
    private ResumeVO resume;
} 