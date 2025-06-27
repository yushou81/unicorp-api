package com.csu.unicorp.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 项目视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectVO {
    
    /**
     * 项目ID
     */
    private Integer id;
    
    /**
     * 所属组织ID
     */
    private Integer organizationId;
    
    /**
     * 所属组织名称
     */
    private String organizationName;
    
    /**
     * 项目标题
     */
    private String title;
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 项目状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
} 