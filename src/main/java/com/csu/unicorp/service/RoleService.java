package com.csu.unicorp.service;

import com.csu.unicorp.entity.Role;

/**
 * 角色服务接口
 */
public interface RoleService {
    
    /**
     * 根据角色名称获取角色
     * 
     * @param roleName 角色名称
     * @return 角色实体
     */
    Role getByRoleName(String roleName);
    
    /**
     * 为用户分配角色
     * 
     * @param userId 用户ID
     * @param roleName 角色名称
     */
    void assignRoleToUser(Integer userId, String roleName);
} 