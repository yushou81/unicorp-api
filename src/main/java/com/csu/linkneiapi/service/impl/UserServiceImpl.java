package com.csu.linkneiapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.linkneiapi.dto.UserDTO;
import com.csu.linkneiapi.entity.User;
import com.csu.linkneiapi.mapper.UserMapper;
import com.csu.linkneiapi.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void register(UserDTO userDTO) {
        // 1. 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        long count = this.count(queryWrapper);
        if (count > 0) {
            // 如果用户名已存在，可以抛出自定义异常
            // 这里为了简化，我们先直接返回，在Controller层处理
            throw new RuntimeException("用户名已存在");
        }

        // 2. 对密码进行加密
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        // 3. 创建新的User对象并保存到数据库
        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(encodedPassword);

        // this.save() 是MyBatis-Plus提供的方法，可以直接保存对象
        this.save(newUser);
    }
}
