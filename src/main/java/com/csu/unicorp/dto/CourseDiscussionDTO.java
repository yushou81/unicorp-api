package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 课程讨论数据传输对象
 */
@Data
@Schema(description = "课程讨论数据传输对象")
public class CourseDiscussionDTO {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID", required = true, example = "1")
    private Integer courseId;
    
    /**
     * 讨论内容
     */
    @NotBlank(message = "讨论内容不能为空")
    @Size(min = 2, max = 1000, message = "讨论内容长度应在2-1000个字符之间")
    @Schema(description = "讨论内容", required = true, example = "这个问题我想了解更多...")
    private String content;
    
    /**
     * 父讨论ID，用于回复
     */
    @Schema(description = "父讨论ID，用于回复", example = "5")
    private Integer parentId;
} 