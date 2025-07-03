package com.csu.unicorp.dto.achievement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 作品资源上传DTO
 */
@Data
public class PortfolioResourceUploadDTO {
    
    /**
     * 资源类型（如：图片、视频、文档等）
     */
    @NotBlank(message = "资源类型不能为空")
    @Size(max = 20, message = "资源类型不能超过20个字符")
    private String resourceType;
    
    /**
     * 资源URL
     */
    @NotBlank(message = "资源URL不能为空")
    @Size(max = 255, message = "资源URL不能超过255个字符")
    private String resourceUrl;
    
    /**
     * 资源描述
     */
    @Size(max = 255, message = "资源描述不能超过255个字符")
    private String description;
    
    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    private Integer displayOrder = 0;
} 