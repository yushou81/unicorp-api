package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 作品集项目实体类
 */
@Data
@TableName("portfolio_items")
public class PortfolioItem {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 所属学生的用户ID
     */
    private Integer userId;
    
    /**
     * 作品或项目标题
     */
    private String title;
    
    /**
     * 详细描述
     */
    private String description;
    
    /**
     * 项目链接
     */
    private String projectUrl;
    
    /**
     * 封面图URL
     */
    private String coverImageUrl;
    
    /**
     * 是否已删除
     */
    @TableLogic
    private Boolean isDeleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 