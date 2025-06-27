package com.csu.unicorp.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 岗位申请详情VO
 */
@Data
public class ApplicationDetailVO {
    /**
     * 申请ID
     */
    private Integer id;
    
    /**
     * 岗位ID
     */
    private Integer jobId;
    
    /**
     * 学生ID
     */
    private Integer studentId;
    
    /**
     * 申请状态
     */
    private String status;
    
    /**
     * 申请时间
     */
    private LocalDateTime appliedAt;
    
    /**
     * 学生资料
     */
    private StudentProfileVO studentProfile;
    
    /**
     * 学生档案VO
     */
    @Data
    public static class StudentProfileVO {
        /**
         * 昵称
         */
        private String nickname;
        
        /**
         * 真实姓名
         */
        private String realName;
        
        /**
         * 专业
         */
        private String major;
        
        /**
         * 教育水平
         */
        private String educationLevel;
        
        /**
         * 简历URL
         */
        private String resumeUrl;
    }
} 