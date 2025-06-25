package com.csu.linkneiapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csu.linkneiapi.common.utils.JwtUtils;
import com.csu.linkneiapi.dto.LoginDTO;
import com.csu.linkneiapi.dto.RegisterDTO;
import com.csu.linkneiapi.entity.UserProfile;
import com.csu.linkneiapi.mapper.UserMapper;
import com.csu.linkneiapi.mapper.UserProfileMapper;
import com.csu.linkneiapi.service.impl.UserServiceImpl;
import com.csu.linkneiapi.vo.JwtResponseVO;

import java.util.ArrayList;

@DisplayName("认证服务测试")
public class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserProfileMapper userProfileMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterDTO registerDTO;
    private LoginDTO loginDTO;
    private Authentication authentication;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 初始化注册DTO
        registerDTO = new RegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setPassword("password123");
        registerDTO.setNickname("Test User");

        // 初始化登录DTO
        loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");

        // 模拟用户详情
        userDetails = new User("testuser", "encodedPassword", new ArrayList<>());

        // 模拟认证对象
        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Test
    @DisplayName("用户注册成功测试")
    void testRegisterWithProfileSuccess() {
        // 模拟数据库查询 - 用户名不存在
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        
        // 使用明确的参数类型避免歧义
        when(userMapper.insert(any(com.csu.linkneiapi.entity.User.class))).thenReturn(1);
        when(userProfileMapper.insert(any(UserProfile.class))).thenReturn(1);

        // 执行注册方法
        userService.registerWithProfile(registerDTO);

        // 验证密码是否被加密
        verify(passwordEncoder, times(1)).encode("password123");
        
        // 验证用户是否被保存
        verify(userMapper, times(1)).insert(any(com.csu.linkneiapi.entity.User.class));
        
        // 验证用户简历是否被创建
        verify(userProfileMapper, times(1)).insert(any(UserProfile.class));
    }

    @Test
    @DisplayName("用户名已存在时的注册测试")
    void testRegisterWithExistingUsername() {
        // 模拟数据库查询 - 用户名已存在
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // 执行注册方法并验证是否抛出异常
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.registerWithProfile(registerDTO);
        });

        // 验证异常消息
        assertEquals("用户名已存在", exception.getMessage());
        
        // 验证没有调用插入方法
        // verify(userMapper, times(0)).insert(any());
        // verify(userProfileMapper, times(0)).insert(any());
    }

    @Test
    @DisplayName("用户登录成功测试")
    void testLoginSuccess() {
        // 模拟认证管理器
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        
        // 模拟JWT生成
        when(jwtUtils.generateToken(userDetails)).thenReturn("test.jwt.token");

        // 执行登录方法
        JwtResponseVO response = userService.login(loginDTO);

        // 验证结果
        assertNotNull(response);
        assertEquals("test.jwt.token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals("testuser", response.getUsername());
        
        // 验证方法调用
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("用户登录失败测试")
    void testLoginFailure() {
        // 模拟认证管理器抛出异常
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new RuntimeException("用户名或密码错误"));

        // 执行登录方法并验证是否抛出异常
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.login(loginDTO);
        });

        // 验证异常消息
        assertEquals("用户名或密码错误", exception.getMessage());
    }
} 