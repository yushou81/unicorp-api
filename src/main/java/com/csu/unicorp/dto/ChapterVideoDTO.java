package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 章节视频数据传输对象
 */
@Data
@Schema(description = "章节视频数据传输对象")
public class ChapterVideoDTO {
    
    /**
     * 章节ID
     */
    @NotNull(message = "章节ID不能为空")
    @Schema(description = "章节ID", required = true, example = "1")
    private Integer chapterId;
    
    /**
     * 视频标题
     */
    @NotBlank(message = "视频标题不能为空")
    @Size(min = 2, max = 100, message = "视频标题长度应在2-100个字符之间")
    @Schema(description = "视频标题", required = true, example = "第一章：Java基础入门视频")
    private String title;
    
    /**
     * 视频描述
     */
    @Size(max = 500, message = "视频描述不能超过500个字符")
    @Schema(description = "视频描述", example = "本视频介绍Java语言的基础知识，包括变量、数据类型、运算符等")
    private String description;
} 