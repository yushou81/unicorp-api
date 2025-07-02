package com.csu.unicorp.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 角色权限关联实体类，对应role_permissions表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("role_permissions")
public class RolePermission {
    /**
     * 角色ID
     */
    private Integer roleId;
    
    /**
     * 权限ID
     */
    private Integer permissionId;
} 