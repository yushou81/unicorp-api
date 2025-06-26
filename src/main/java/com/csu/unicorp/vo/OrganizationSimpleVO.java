package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 精简版组织VO类，用于下拉列表选择
 */
@Data
@Schema(description = "精简版组织信息")
public class OrganizationSimpleVO {
    
    /**
     * 组织ID
     */
    @Schema(description = "组织ID")
    private Integer id;
    
    /**
     * 组织名称
     */
    @Schema(description = "组织名称", example = "中南大学")
    private String organizationName;
} 