package com.csu.unicorp.service.impl;

import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 自定义用户详情服务实现
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserMapper userMapper;
    
    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        // 根据账号查询用户
        User user = userMapper.selectByAccount(account);
        
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + account);
        }
        
        // 查询用户角色
        String role = userMapper.selectRoleByUserId(user.getId());
        
        // 创建并返回自定义UserDetails对象
        return new CustomUserDetails(user, role);
    }
} 