package com.csu.unicorp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 项目创建数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreationDTO {
    
    /**
     * 项目标题
     */
    @NotBlank(message = "项目标题不能为空")
    @Size(max = 255, message = "项目标题长度不能超过255个字符")
    private String title;
    
    /**
     * 项目描述
     */
    @NotBlank(message = "项目描述不能为空")
    private String description;
    
    /**
     * 项目状态
     */
    private String status;
} 