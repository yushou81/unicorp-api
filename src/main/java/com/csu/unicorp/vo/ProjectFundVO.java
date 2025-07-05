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
}
