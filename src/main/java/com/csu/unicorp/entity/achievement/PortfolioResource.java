package com.csu.unicorp.entity.achievement;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 作品资源实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("portfolio_resources")
public class PortfolioResource {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
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