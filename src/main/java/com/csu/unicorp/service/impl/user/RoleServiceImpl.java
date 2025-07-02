package com.csu.unicorp.service.impl.user;

import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.entity.user.Role;
import com.csu.unicorp.entity.user.UserRole;
import com.csu.unicorp.mapper.RoleMapper;
import com.csu.unicorp.mapper.UserRoleMapper;
import com.csu.unicorp.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色服务实现类
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    
    @Override
    public Role getByRoleName(String roleName) {
        return roleMapper.selectByRoleName(roleName);
    }
    
    @Override
    @Transactional
    public void assignRoleToUser(Integer userId, String roleName) {
        Role role = getByRoleName(roleName);
        if (role == null) {
            throw new BusinessException("角色不存在: " + roleName);
        }
        
        // 检查是否已经分配了该角色
        if (hasUserRole(userId, role.getId())) {
            return; // 已存在该角色，不重复添加
        }
        
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(role.getId());
        
        userRoleMapper.insert(userRole);
    }
    
    /**
     * 检查用户是否已经拥有指定角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否拥有角色
     */
    private boolean hasUserRole(Integer userId, Integer roleId) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        
        return userRoleMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>(userRole)
        ) > 0;
    }
} 