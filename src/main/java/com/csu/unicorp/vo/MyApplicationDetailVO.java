package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 我的岗位申请详情VO，用于学生查看自己的申请记录
 */
@Data
@Schema(description = "我的岗位申请详情")
public class MyApplicationDetailVO {
    
    /**
     * 申请ID
     */
    @Schema(description = "申请ID")
    private Integer applicationId;
    
    /**
     * 申请状态
     */
    @Schema(description = "申请状态", example = "submitted")
    private String status;
    
    /**
     * 申请时间
     */
    @Schema(description = "申请时间")
    private LocalDateTime appliedAt;
    
    /**
     * 岗位信息
     */
    @Schema(description = "岗位信息")
    private JobInfo jobInfo;
    
    /**
     * 岗位信息内部类
     */
    @Data
    @Schema(description = "岗位基本信息")
    public static class JobInfo {
        /**
         * 岗位ID
         */
        @Schema(description = "岗位ID")
        private Integer jobId;
        
        /**
         * 岗位标题
         */
        @Schema(description = "岗位标题", example = "Java开发工程师")
        private String jobTitle;
        
        /**
         * 企业名称
         */
        @Schema(description = "企业名称", example = "阿里巴巴")
        private String organizationName;
    }
} 