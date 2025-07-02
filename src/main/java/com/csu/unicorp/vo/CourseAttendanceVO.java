package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 课程出勤视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程出勤视图对象")
public class CourseAttendanceVO {
    
    /**
     * 出勤记录ID
     */
    @Schema(description = "出勤记录ID", example = "1")
    private Integer id;
    
    /**
     * 课程ID
     */
    @Schema(description = "课程ID", example = "1")
    private Integer courseId;
    
    /**
     * 课程标题
     */
    @Schema(description = "课程标题", example = "Java企业级开发实战")
    private String courseTitle;
    
    /**
     * 学生ID
     */
    @Schema(description = "学生ID", example = "5")
    private Integer studentId;
    
    /**
     * 学生姓名
     */
    @Schema(description = "学生姓名", example = "张三")
    private String studentName;
    
    /**
     * 出勤日期
     */
    @Schema(description = "出勤日期", example = "2024-06-15")
    private LocalDate attendanceDate;
    
    /**
     * 出勤状态
     */
    @Schema(description = "出勤状态", example = "present")
    private String status;
    
    /**
     * 备注
     */
    @Schema(description = "备注", example = "迟到15分钟")
    private String remark;
    
    /**
     * 记录人ID
     */
    @Schema(description = "记录人ID", example = "10")
    private Integer recordedBy;
    
    /**
     * 记录人姓名
     */
    @Schema(description = "记录人姓名", example = "李四")
    private String recorderName;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-06-15T10:15:30")
    private LocalDateTime createdAt;
} 