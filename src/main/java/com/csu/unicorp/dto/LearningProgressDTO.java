package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 学习进度数据传输对象
 */
@Data
@Schema(description = "学习进度数据传输对象")
public class LearningProgressDTO {
    
    /**
     * 章节ID
     */
    @NotNull(message = "章节ID不能为空")
    @Schema(description = "章节ID", required = true, example = "1")
    private Integer chapterId;
    
    /**
     * 学习状态
     */
    @NotNull(message = "学习状态不能为空")
    @Schema(description = "学习状态", required = true, example = "in_progress", 
            allowableValues = {"not_started", "in_progress", "completed"})
    private String status;
    
    /**
     * 完成百分比
     */
    @Min(value = 0, message = "完成百分比不能小于0")
    @Max(value = 100, message = "完成百分比不能大于100")
    @Schema(description = "完成百分比(0-100)", example = "75")
    private Integer progressPercent;
    
    /**
     * 学习时长(分钟)
     */
    @Min(value = 0, message = "学习时长不能为负数")
    @Schema(description = "学习时长(分钟)", example = "45")
    private Integer durationMinutes;
} 