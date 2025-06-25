package com.csu.linkneiapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.linkneiapi.common.utils.JwtUtils;
import com.csu.linkneiapi.dto.LoginDTO;
import com.csu.linkneiapi.dto.ProfileUpdateDTO;
import com.csu.linkneiapi.dto.RegisterDTO;
import com.csu.linkneiapi.entity.User;
import com.csu.linkneiapi.entity.UserProfile;
import com.csu.linkneiapi.mapper.UserMapper;
import com.csu.linkneiapi.mapper.UserProfileMapper;
import com.csu.linkneiapi.service.UserService;
import com.csu.linkneiapi.vo.JwtResponseVO;
import com.csu.linkneiapi.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserProfileMapper userProfileMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registerWithProfile(RegisterDTO registerDTO) {
        // 1. 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, registerDTO.getUsername());
        long usernameCount = this.count(usernameWrapper);
        if (usernameCount > 0) {
            // 如果用户名已存在，抛出异常
            throw new RuntimeException("用户名已存在");
        }

        // 2. 对密码进行加密
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());

        // 3. 创建新的User对象
        User newUser = new User();
        newUser.setUsername(registerDTO.getUsername());
        newUser.setPassword(encodedPassword);
        newUser.setNickname(registerDTO.getNickname()); // 设置初始昵称（如果有）
        newUser.setStatus(0);    // 设置默认状态为正常

        // 4. 保存用户
        this.save(newUser);
        
        // 5. 创建空的用户简历档案
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(newUser.getId());
        
        // 6. 保存用户简历档案
        userProfileMapper.insert(userProfile);
    }
    
    @Override
    public JwtResponseVO login(LoginDTO loginDTO) {
        try {
            // 1. 使用AuthenticationManager进行用户认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getUsername(),
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
            // 认证失败，用户名或密码错误
            throw new RuntimeException("用户名或密码错误");
        }
    }
    
    @Override
    public UserProfileVO getUserProfile(String username) {
        // 假设的实现，真实实现需要查询数据库
        return new UserProfileVO();
    }
    
    @Override
    public void updateUserProfile(String username, ProfileUpdateDTO profileUpdateDTO) {
        // 假设的实现，真实实现需要更新数据库
    }
}
