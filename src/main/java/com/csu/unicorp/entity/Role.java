package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 角色实体类，对应roles表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("roles")
public class Role {
    /**
     * 角色ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 角色名称
     */
    private String roleName;
} 