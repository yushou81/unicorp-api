package com.csu.unicorp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 课程出勤数据传输对象
 */
@Data
@Schema(description = "课程出勤数据传输对象")
public class CourseAttendanceDTO {
    
    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID", required = true, example = "1")
    private Integer courseId;
    
    /**
     * 出勤日期
     */
    @NotNull(message = "出勤日期不能为空")
    @Schema(description = "出勤日期", required = true, example = "2024-06-15")
    private LocalDate attendanceDate;
    
    /**
     * 学生出勤记录
     */
    @Schema(description = "学生出勤记录")
    private List<StudentAttendanceRecord> studentRecords;
    
    /**
     * 备注
     */
    @Size(max = 255, message = "备注不能超过255个字符")
    @Schema(description = "备注", example = "今天是实验课")
    private String remark;
    
    /**
     * 学生出勤记录
     */
    @Data
    @Schema(description = "学生出勤记录")
    public static class StudentAttendanceRecord {
        
        /**
         * 学生ID
         */
        @NotNull(message = "学生ID不能为空")
        @Schema(description = "学生ID", required = true, example = "5")
        private Integer studentId;
        
        /**
         * 出勤状态
         */
        @NotNull(message = "出勤状态不能为空")
        @Schema(description = "出勤状态", required = true, example = "present", 
                allowableValues = {"present", "absent", "late", "leave"})
        private String status;
        
        /**
         * 备注
         */
        @Size(max = 255, message = "备注不能超过255个字符")
        @Schema(description = "备注", example = "迟到15分钟")
        private String remark;
    }
} 