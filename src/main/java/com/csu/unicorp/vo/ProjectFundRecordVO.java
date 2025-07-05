package com.csu.unicorp.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProjectFundRecordVO {
    private Integer fundId;
    private BigDecimal amount;
    private String purpose;
    private String status;
}
