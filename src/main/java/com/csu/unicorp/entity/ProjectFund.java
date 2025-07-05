package com.csu.unicorp.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 项目经费申请
 */
@Data
public class ProjectFund {
    private Integer fundId;
    private Integer projectId;
    private BigDecimal amount;
    private String purpose;
    private Integer applicantId;
    private String attachments; // 逗号分隔的URL
    private String status; // pending/approved/rejected
}
