package com.csu.unicorp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 用户角色关联实体类，对应user_roles表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("roles")
public class UserRole {
    /**
     * 用户ID
     */
    private Integer userId;
    
    /**
     * 角色ID
     */
    private Integer roleId;
} 