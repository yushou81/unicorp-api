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
     * 学生档案信息
     */
    @Schema(description = "学生档案信息")
    private StudentProfileVO studentProfile;
    
    /**
     * 学生档案信息VO
     */
    @Data
    @Schema(description = "学生档案信息VO")
    public static class StudentProfileVO {
        /**
         * 昵称
         */
        @Schema(description = "昵称")
        private String nickname;
        
        /**
         * 真实姓名
         */
        @Schema(description = "真实姓名")
        private String realName;
        
        /**
         * 专业
         */
        @Schema(description = "专业")
        private String major;
        
        /**
         * 教育水平
         */
        @Schema(description = "教育水平")
        private String educationLevel;
        
        /**
         * 简历URL
         */
        @Schema(description = "简历URL")
        private String resumeUrl;
    }
} 