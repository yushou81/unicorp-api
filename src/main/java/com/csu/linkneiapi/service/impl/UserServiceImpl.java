package com.csu.linkneiapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.linkneiapi.common.utils.JwtUtils;
import com.csu.linkneiapi.dto.LoginDTO;
import com.csu.linkneiapi.dto.UserDTO;
import com.csu.linkneiapi.entity.User;
import com.csu.linkneiapi.mapper.UserMapper;
import com.csu.linkneiapi.service.UserService;
import com.csu.linkneiapi.vo.JwtResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void register(UserDTO userDTO) {
        // 1. 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, userDTO.getUsername());
        long usernameCount = this.count(usernameWrapper);
        if (usernameCount > 0) {
            // 如果用户名已存在，抛出异常
            throw new RuntimeException("用户名已存在");
        }

        // 2. 检查手机号是否已存在
        LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
        phoneWrapper.eq(User::getPhone, userDTO.getPhone());
        long phoneCount = this.count(phoneWrapper);
        if (phoneCount > 0) {
            // 如果手机号已存在，抛出异常
            throw new RuntimeException("手机号已被注册");
        }

        // 3. 对密码进行加密
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

        // 4. 创建新的User对象并保存到数据库
        User newUser = new User();
        newUser.setUsername(userDTO.getUsername());
        newUser.setPassword(encodedPassword);
        newUser.setPhone(userDTO.getPhone());
        newUser.setRole("USER"); // 设置默认角色
        newUser.setStatus(0);    // 设置默认状态为正常

        // this.save() 是MyBatis-Plus提供的方法，可以直接保存对象
        this.save(newUser);
    }
    
    @Override
    public JwtResponseVO login(LoginDTO loginDTO) {
        try {
            // 获取登录类型和凭据
            String loginCredential = loginDTO.getUsername();
            String loginType = loginDTO.getLoginType();
            
            // 1. 使用AuthenticationManager进行用户认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginCredential,
                            loginDTO.getPassword()
                    )
            );
            
            // 2. 认证成功，获取UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // 3. 生成JWT令牌
            String jwt = jwtUtils.generateToken(userDetails);
            
            // 4. 返回JWT响应对象
            return new JwtResponseVO(jwt, "Bearer", userDetails.getUsername());
            
        } catch (BadCredentialsException e) {
            // 认证失败，用户名/手机号或密码错误
            throw new RuntimeException("用户名/手机号或密码错误");
        }
    }
}
