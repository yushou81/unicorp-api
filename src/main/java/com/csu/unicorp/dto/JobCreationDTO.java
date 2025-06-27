package com.csu.unicorp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 岗位创建DTO
 */
@Data
public class JobCreationDTO {
    /**
     * 岗位标题
     */
    @NotBlank(message = "岗位标题不能为空")
    private String title;
    
    /**
     * 岗位描述
     */
    @NotBlank(message = "岗位描述不能为空")
    private String description;
    
    /**
     * 工作地点
     */
    private String location;
} 