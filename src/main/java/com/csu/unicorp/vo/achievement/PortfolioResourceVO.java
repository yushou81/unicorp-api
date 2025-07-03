package com.csu.unicorp.vo.achievement;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 作品资源VO
 */
@Data
public class PortfolioResourceVO {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 关联的作品项目ID
     */
    private Integer portfolioItemId;
    
    /**
     * 资源类型（如：图片、视频、文档等）
     */
    private String resourceType;
    
    /**
     * 资源URL
     */
    private String resourceUrl;
    
    /**
     * 资源描述
     */
    private String description;
    
    /**
     * 显示顺序
     */
    private Integer displayOrder;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
} 