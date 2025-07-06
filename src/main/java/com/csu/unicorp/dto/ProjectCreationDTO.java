package com.csu.unicorp.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 发布项目DTO
 */
@Data
public class ProjectCreationDTO {
    private String title;
    private String description;
    private String initiatorType;
    private Integer initiatorId;
    private String field;
    private BigDecimal budget;
    private String contact;
    private List<String> attachments;
    private String status; // 新增
    private Integer projectId; // 新增
    private String reason; // 状态变更原因
}
