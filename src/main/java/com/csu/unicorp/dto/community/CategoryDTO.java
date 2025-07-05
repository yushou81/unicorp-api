package com.csu.unicorp.dto.community;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 社区板块DTO
 */
@Data
@Schema(description = "社区板块DTO")
public class CategoryDTO {
    
    /**
     * 板块名称
     */
    @NotBlank(message = "板块名称不能为空")
    @Size(max = 100, message = "板块名称不能超过100个字符")
    @Schema(description = "板块名称", required = true, example = "技术交流")
    private String name;
    
    /**
     * 板块描述
     */
    @Size(max = 500, message = "板块描述不能超过500个字符")
    @Schema(description = "板块描述", example = "讨论各种技术问题和经验分享")
    private String description;
    
    /**
     * 板块图标
     */
    @Schema(description = "板块图标URL", example = "/icons/tech.png")
    private String icon;
    
    /**
     * 排序顺序
     */
    @Schema(description = "排序顺序", example = "1")
    private Integer sortOrder;
    
    /**
     * 父板块ID
     */
    @Schema(description = "父板块ID", example = "1")
    private Long parentId;
    
    /**
     * 权限级别：0-公开，1-登录可见，2-组织成员可见，3-管理员可见
     */
    @Schema(description = "权限级别：0-公开，1-登录可见，2-组织成员可见，3-管理员可见", example = "0")
    private Integer permissionLevel;
} 