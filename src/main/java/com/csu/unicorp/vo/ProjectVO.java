package com.csu.unicorp.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * 项目VO
 */
@Data
public class ProjectVO {
    private Integer projectId;
    private String title;
    private String description;
    private String initiatorType;
    private Integer initiatorId;
    private String field;
    private BigDecimal budget;
    private String contact;
    private List<String> attachments;
    private String status;
    private String organizationName; // 新增
    private String reason;
    private Timestamp createTime; // 创建时间
    private Timestamp updateTime; // 更新时间
    private Boolean hasApplied; // 是否已申请
private String applicationStatus; // 申请状态（可选）
}
