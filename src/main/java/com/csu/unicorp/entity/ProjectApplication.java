package com.csu.unicorp.entity;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 项目对接/合作申请
 */
@Data
public class ProjectApplication {
    @TableId
    private Integer applicationId;
    private Integer projectId;
    private String applicantType;
    private Integer applicantId;
    private String message;
    private String status; // pending/approved/rejected
    private Timestamp createTime; // 新增
    private Timestamp updateTime; // 新增
    private Timestamp approvedTime; // 新增：同意时间
}
