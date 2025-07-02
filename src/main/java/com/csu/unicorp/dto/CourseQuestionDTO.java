package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 课程问答数据传输对象
 */
@Data
@Schema(description = "课程问答数据传输对象")
public class CourseQuestionDTO {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID", required = true, example = "1")
    private Integer courseId;
    
    /**
     * 相关章节ID
     */
    @Schema(description = "相关章节ID", example = "2")
    private Integer chapterId;
    
    /**
     * 问题标题
     */
    @NotBlank(message = "问题标题不能为空")
    @Size(min = 5, max = 200, message = "问题标题长度应在5-200个字符之间")
    @Schema(description = "问题标题", required = true, example = "Java中的多态是如何实现的？")
    private String title;
    
    /**
     * 问题内容
     */
    @NotBlank(message = "问题内容不能为空")
    @Size(min = 10, max = 2000, message = "问题内容长度应在10-2000个字符之间")
    @Schema(description = "问题内容", required = true, example = "我对Java中的多态概念有些疑惑，特别是关于接口和抽象类的使用...")
    private String content;
} 