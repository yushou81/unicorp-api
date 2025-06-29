package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 我的岗位申请详情VO
 */
@Data
@Schema(description = "我的岗位申请详情VO")
public class MyApplicationDetailVO {
    
    /**
     * 申请ID
     */
    @Schema(description = "申请ID")
    private Integer applicationId;
    
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
     * 岗位信息
     */
    @Schema(description = "岗位信息")
    private JobInfoVO jobInfo;
    
    /**
     * 岗位信息VO
     */
    @Data
    @Schema(description = "岗位信息VO")
    public static class JobInfoVO {
        
        /**
         * 岗位ID
         */
        @Schema(description = "岗位ID")
        private Integer jobId;
        
        /**
         * 岗位标题
         */
        @Schema(description = "岗位标题")
        private String jobTitle;
        
        /**
         * 组织名称
         */
        @Schema(description = "组织名称")
        private String organizationName;
    }
} 