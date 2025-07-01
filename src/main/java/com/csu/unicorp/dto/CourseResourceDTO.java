package com.csu.unicorp.dto;

import com.csu.unicorp.entity.enums.ResourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 课程资源数据传输对象
 */
@Data
@Schema(description = "课程资源数据传输对象")
public class CourseResourceDTO {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID", required = true, example = "1")
    private Integer courseId;
    
    /**
     * 资源标题
     */
    @NotBlank(message = "资源标题不能为空")
    @Size(min = 2, max = 100, message = "资源标题长度应在2-100个字符之间")
    @Schema(description = "资源标题", required = true, example = "Java企业级开发实战课件")
    private String title;
    
    /**
     * 资源描述
     */
    @Size(max = 500, message = "资源描述不能超过500个字符")
    @Schema(description = "资源描述", example = "本课件包含Java企业级开发实战的核心内容和示例代码")
    private String description;
    
    /**
     * 资源类型
     */
    @NotNull(message = "资源类型不能为空")
    @Schema(description = "资源类型", required = true, example = "DOCUMENT", allowableValues = {"DOCUMENT", "VIDEO", "CODE", "OTHER"})
    private ResourceType resourceType;
} 