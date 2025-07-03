package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源创建数据传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "资源创建请求")
public class ResourceCreationDTO {
    
    /**
     * 资源标题
     */
    @NotBlank(message = "资源标题不能为空")
    @Size(min = 2, max = 100, message = "标题长度必须在2-100个字符之间")
    @Schema(description = "资源标题", required = true)
    private String title;
    
    /**
     * 资源描述
     */
    @Size(max = 500, message = "描述长度不能超过500个字符")
    @Schema(description = "资源描述")
    private String description;
    
    /**
     * 资源类型
     */
    @NotBlank(message = "资源类型不能为空")
    @Schema(description = "资源类型，如技术文档、教学课件、案例分析、专利、著作权等", required = true)
    private String resourceType;
    
    /**
     * 文件URL
     */
    @Schema(description = "文件上传后获取到的URL")
    private String fileUrl;
    
    /**
     * 图片URL（用于专利、著作权等类型资源的图片展示）
     */
    @Schema(description = "图片URL，用于专利、著作权等类型资源的图片展示")
    private String imageUrl;
    
    /**
     * 可见性
     */
    @Schema(description = "可见性，可选值：public（公开）、private（私有）、organization_only（仅组织内可见），默认为public", defaultValue = "public")
    private String visibility;
} 