package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 学生档案VO
 */
@Data
@Schema(description = "学生档案VO")
public class StudentProfileVO {
    
    /**
     * 专业
     */
    @Schema(description = "专业")
    private String major;
    
    /**
     * 教育水平
     */
    @Schema(description = "教育水平")
    private String educationLevel;
} 