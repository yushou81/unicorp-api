package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 课程评价数据传输对象
 */
@Data
@Schema(description = "课程评价数据传输对象")
public class CourseRatingDTO {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID", required = true, example = "1")
    private Integer courseId;
    
    /**
     * 评分(1-5)
     */
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    @Schema(description = "评分(1-5)", required = true, example = "5")
    private Integer rating;
    
    /**
     * 评价内容
     */
    @Size(max = 500, message = "评价内容不能超过500个字符")
    @Schema(description = "评价内容", example = "课程内容丰富，讲解清晰，收获很大")
    private String comment;
    
    /**
     * 是否匿名
     */
    @Schema(description = "是否匿名", defaultValue = "false", example = "false")
    private Boolean isAnonymous = false;
} 