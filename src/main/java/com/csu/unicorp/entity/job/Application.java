package com.csu.unicorp.entity.job;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 岗位申请实体类，对应applications表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("applications")
public class Application {
    /**
     * 申请ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 岗位ID
     */
    private Integer jobId;
    
    /**
     * 学生ID（用户ID）
     */
    private Integer studentId;
    
    /**
     * 关联的简历ID
     */
    private Integer resumeId;
    
    /**
     * 申请状态：submitted-已提交，viewed-已查看，interviewing-面试中，offered-已录用，rejected-已拒绝
     */
    private String status;
    
    /**
     * 申请时间
     */
    private LocalDateTime appliedAt;
} 