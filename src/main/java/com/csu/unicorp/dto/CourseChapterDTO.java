package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 课程章节数据传输对象
 */
@Data
@Schema(description = "课程章节数据传输对象")
public class CourseChapterDTO {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID", required = true, example = "1")
    private Integer courseId;
    
    /**
     * 章节标题
     */
    @NotBlank(message = "章节标题不能为空")
    @Size(min = 2, max = 100, message = "章节标题长度应在2-100个字符之间")
    @Schema(description = "章节标题", required = true, example = "第一章：Java基础")
    private String title;
    
    /**
     * 章节描述
     */
    @Size(max = 500, message = "章节描述不能超过500个字符")
    @Schema(description = "章节描述", example = "本章介绍Java语言基础知识，包括变量、数据类型、运算符等")
    private String description;
    
    /**
     * 章节顺序
     */
    @Schema(description = "章节顺序", example = "1")
    private Integer sequence;
    
    /**
     * 是否发布
     */
    @Schema(description = "是否发布", example = "false")
    private Boolean isPublished;
    
    /**
     * 章节关联的资源ID列表
     */
    @Schema(description = "章节关联的资源ID列表", example = "[1, 2, 3]")
    private List<Integer> resourceIds;
} 