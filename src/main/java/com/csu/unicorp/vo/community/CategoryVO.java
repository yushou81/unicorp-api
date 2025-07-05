package com.csu.unicorp.vo.community;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社区板块视图对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "社区板块视图对象")
public class CategoryVO {
    
    /**
     * 板块ID
     */
    @Schema(description = "板块ID", example = "1")
    private Long id;
    
    /**
     * 板块名称
     */
    @Schema(description = "板块名称", example = "技术交流")
    private String name;
    
    /**
     * 板块描述
     */
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
     * 父板块名称
     */
    @Schema(description = "父板块名称", example = "综合交流")
    private String parentName;
    
    /**
     * 权限级别：0-公开，1-登录可见，2-组织成员可见，3-管理员可见
     */
    @Schema(description = "权限级别：0-公开，1-登录可见，2-组织成员可见，3-管理员可见", example = "0")
    private Integer permissionLevel;
    
    /**
     * 子板块列表
     */
    @Schema(description = "子板块列表")
    private List<CategoryVO> children;
    
    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2023-01-01 12:00:00")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2023-01-01 12:00:00")
    private LocalDateTime updatedAt;
    
    /**
     * 话题数量
     */
    @Schema(description = "话题数量", example = "100")
    private Integer topicCount;
} 