package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Spring Security UserDetailsService实现类
 * 用于验证用户身份
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户 - 尝试通过用户名和手机号两种方式查找
        User user = findUserByUsernameOrPhone(username);
        
        // 如果用户不存在，抛出异常
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        // 返回UserDetails对象
        // 参数说明：用户名、密码、权限列表(目前为空，后续可扩展)
        return new org.springframework.security.core.userdetails.User(
                user.getAccount(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
    
    /**
     * 通过用户名或手机号查找用户
     * @param usernameOrPhone 用户名或手机号
     * @return 用户实体，如果不存在返回null
     */
    private User findUserByUsernameOrPhone(String usernameOrPhone) {
        // 先尝试通过用户名查找
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getAccount, usernameOrPhone);
        User user = userMapper.selectOne(usernameWrapper);
        
        // 如果通过用户名没找到，再尝试通过手机号查找
        if (user == null) {
            LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(User::getPhone, usernameOrPhone);
            user = userMapper.selectOne(phoneWrapper);
        }
        
        return user;
    }
} 