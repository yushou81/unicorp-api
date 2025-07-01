package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程评价视图对象
 */
@Data
@Schema(description = "课程评价视图对象")
public class CourseRatingVO {
    
    /**
     * 评价ID
     */
    @Schema(description = "评价ID", example = "1")
    private Integer id;
    
    /**
     * 课程ID
     */
    @Schema(description = "课程ID", example = "1")
    private Integer courseId;
    
    /**
     * 学生ID
     */
    @Schema(description = "学生ID", example = "8")
    private Integer studentId;
    
    /**
     * 学生姓名
     */
    @Schema(description = "学生姓名", example = "李明")
    private String studentName;
    
    /**
     * 评分(1-5)
     */
    @Schema(description = "评分(1-5)", example = "5")
    private Integer rating;
    
    /**
     * 评价内容
     */
    @Schema(description = "评价内容", example = "课程内容丰富，讲解清晰，收获很大")
    private String comment;
    
    /**
     * 是否匿名
     */
    @Schema(description = "是否匿名", example = "false")
    private Boolean isAnonymous;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-06-01T10:15:30")
    private LocalDateTime createdAt;
} 