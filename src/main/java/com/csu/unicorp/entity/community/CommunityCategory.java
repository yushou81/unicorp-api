package com.csu.unicorp.entity.community;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社区板块实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("community_category")
public class CommunityCategory implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 板块名称
     */
    private String name;
    
    /**
     * 板块描述
     */
    private String description;
    
    /**
     * 板块图标
     */
    private String icon;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 父板块ID
     */
    private Long parentId;
    
    /**
     * 权限级别：0-公开，1-登录可见，2-组织成员可见，3-管理员可见
     */
    private Integer permissionLevel;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
} 