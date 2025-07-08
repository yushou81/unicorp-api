package com.csu.unicorp.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

/**
 * 项目主表
 */
@Data
public class Project {
    @TableId(type = IdType.AUTO)
    private Integer projectId;
    private String title;
    private String description;
    private String initiatorType; // school/enterprise
    private Integer initiatorId;
    private String field;
    private BigDecimal budget;
    private String contact;
    private String status; // pending/active/closed/rejected
    private String attachments; // 逗号分隔的URL
    private String reason; // 状态变更原因
    private Timestamp createTime; // 创建时间   
private Timestamp updateTime; // 更新时间
}