package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 权限实体类，对应permissions表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("permissions")
public class Permission {
    /**
     * 权限ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 权限名称
     */
    private String permissionName;
    
    /**
     * 权限描述
     */
    private String description;
} 