package com.csu.unicorp.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 我的项目申请详情视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyProjectApplicationDetailVO {
    
    /**
     * 申请ID
     */
    private Integer applicationId;
    
    /**
     * 申请状态
     */
    private String status;
    
    /**
     * 申请时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime appliedAt;
    
    /**
     * 项目信息
     */
    private ProjectInfoVO projectInfo;
    
    /**
     * 项目信息内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProjectInfoVO {
        /**
         * 项目ID
         */
        private Integer projectId;
        
        /**
         * 项目标题
         */
        private String projectTitle;
        
        /**
         * 组织名称
         */
        private String organizationName;
    }
} 