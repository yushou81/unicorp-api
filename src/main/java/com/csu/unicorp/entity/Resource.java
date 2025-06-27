package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 共享资源实体类，对应resources表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("resources")
public class Resource {
    /**
     * 资源ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 上传用户ID
     */
    private Integer uploadedByUserId;
    
    /**
     * 资源标题
     */
    private String title;
    
    /**
     * 资源描述
     */
    private String description;
    
    /**
     * 资源类型，如document、video、equipment
     */
    private String resourceType;
    
    /**
     * 文件URL
     */
    private String fileUrl;
    
    /**
     * 可见性，枚举值：public, private, organization_only
     */
    private String visibility;
    
    /**
     * 逻辑删除标志
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