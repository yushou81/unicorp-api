package com.csu.unicorp.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProjectFundVO {
    private Integer fundId;
    private Integer projectId;
    private BigDecimal amount;
    private String purpose;
    private Integer applicantId;
    private List<String> attachments;
    private String status;
    private java.sql.Timestamp createTime; // 申请创建时间
private java.sql.Timestamp approvedTime; // 同意时间
private java.sql.Timestamp rejectedTime; // 拒绝时间
}
