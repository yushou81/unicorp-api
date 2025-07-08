package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 项目经费申请
 */
@Data
public class ProjectFund {
    @TableId(type = IdType.AUTO)
    private Integer fundId;
    private Integer projectId;
    private BigDecimal amount;
    private String purpose;
    private Integer applicantId;
    private String attachments; // 逗号分隔的URL
    private String status; // pending/approved/rejected
    private java.sql.Timestamp createTime; // 申请创建时间
private java.sql.Timestamp approvedTime; // 同意时间
private java.sql.Timestamp rejectedTime; // 拒绝时间
}
