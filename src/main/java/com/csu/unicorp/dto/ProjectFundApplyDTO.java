package com.csu.unicorp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProjectFundApplyDTO {
    private BigDecimal amount;
    private String purpose;
    private Integer applicantId;
    private List<String> attachments;
    private java.sql.Timestamp createTime; // 申请创建时间
    private String remark;
}
