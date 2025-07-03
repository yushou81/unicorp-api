package com.csu.unicorp.util;

import com.csu.unicorp.entity.organization.Organization;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.UserRole;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 测试数据工具类
 */
@Component
public class TestDataUtil {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrganizationMapper organizationMapper;
    
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 创建测试组织
     */
    @Transactional
    public Organization createTestOrganization(String name, String type) {
        Organization organization = new Organization();
        organization.setOrganizationName(name);
        organization.setType(type);
        organization.setStatus("active");
        organization.setCreatedAt(LocalDateTime.now());
        organization.setUpdatedAt(LocalDateTime.now());
        organization.setIsDeleted(false);
        organizationMapper.insert(organization);
        return organization;
    }

    /**
     * 创建测试用户
     */
    @Transactional
    public User createTestUser(String account, String password, String nickname, Integer organizationId, String roleId) {
        User user = new User();
        user.setAccount(account);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setOrganizationId(organizationId);
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsDeleted(false);
        userMapper.insert(user);
        
        // 创建用户角色关联
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(Integer.parseInt(roleId));
        userRoleMapper.insert(userRole);
        
        return user;
    }
} 