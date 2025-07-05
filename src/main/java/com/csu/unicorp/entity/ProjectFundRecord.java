package com.csu.unicorp.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 经费使用记录
 */
@Data
public class ProjectFundRecord {
    private Integer fundId;
    private BigDecimal amount;
    private String purpose;
    private String status;
}
