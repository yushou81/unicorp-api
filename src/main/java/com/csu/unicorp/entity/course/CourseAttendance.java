package com.csu.unicorp.entity.course;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 课程出勤记录实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("course_attendance")
public class CourseAttendance {
    
    /**
     * 出勤记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    /**
     * 课程ID
     */
    private Integer courseId;
    
    /**
     * 学生ID
     */
    private Integer studentId;
    
    /**
     * 出勤日期
     */
    private LocalDate attendanceDate;
    
    /**
     * 出勤状态：present-出席, absent-缺席, late-迟到, leave-请假
     */
    private String status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 记录人ID
     */
    private Integer recordedBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 