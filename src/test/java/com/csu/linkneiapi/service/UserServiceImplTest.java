package com.csu.linkneiapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csu.linkneiapi.dto.ProfileUpdateDTO;
import com.csu.linkneiapi.entity.User;
import com.csu.linkneiapi.mapper.UserMapper;
import com.csu.linkneiapi.service.impl.UserServiceImpl;
import com.csu.linkneiapi.vo.UserProfileVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 创建模拟用户
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setPassword("password");
        mockUser.setNickname("Test User");
        mockUser.setAvatarUrl("https://example.com/avatar.jpg");
        mockUser.setPhone("13800138000");
        mockUser.setRole("USER");
        mockUser.setStatus(0);
    }

    @Test
    void getUserProfile_Success() {
        // 模拟Mapper行为
        when(userService.getOne(any(LambdaQueryWrapper.class))).thenReturn(mockUser);
        
        // 调用被测试方法
        UserProfileVO result = userService.getUserProfile("testuser");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("Test User", result.getNickname());
        assertEquals("https://example.com/avatar.jpg", result.getAvatarUrl());
        assertEquals("138****8000", result.getPhone()); // 验证手机号脱敏
        assertEquals("USER", result.getRole());
    }
    
    @Test
    void getUserProfile_UserNotExist() {
        // 模拟Mapper行为，返回null表示用户不存在
        when(userService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        
        // 验证抛出异常
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.getUserProfile("nonexistentuser");
        });
        
        assertEquals("用户不存在", exception.getMessage());
    }
    
    @Test
    void updateUserProfile_Success() {
        // 模拟数据
        ProfileUpdateDTO updateDTO = new ProfileUpdateDTO();
        updateDTO.setNickname("New Nickname");
        updateDTO.setAvatarUrl("https://example.com/new_avatar.jpg");
        
        // 模拟Mapper行为
        when(userService.getOne(any(LambdaQueryWrapper.class))).thenReturn(mockUser);
        when(userService.count(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(userService.updateById(any(User.class))).thenReturn(true);
        
        // 调用被测试方法
        userService.updateUserProfile("testuser", updateDTO);
        
        // 验证用户信息更新
        verify(userService, times(1)).updateById(any(User.class));
    }
    
    @Test
    void updateUserProfile_PhoneAlreadyInUse() {
        // 模拟数据
        ProfileUpdateDTO updateDTO = new ProfileUpdateDTO();
        updateDTO.setPhone("13900139000"); // 一个不同的手机号
        
        // 模拟Mapper行为 - 用户存在，但手机号已被其他用户使用
        when(userService.getOne(any(LambdaQueryWrapper.class))).thenReturn(mockUser);
        when(userService.count(any(LambdaQueryWrapper.class))).thenReturn(1L); // 有其他用户使用了该手机号
        
        // 验证抛出异常
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserProfile("testuser", updateDTO);
        });
        
        assertEquals("该手机号已被其他用户使用", exception.getMessage());
    }
    
    @Test
    void updateUserProfile_UserNotExist() {
        // 模拟数据
        ProfileUpdateDTO updateDTO = new ProfileUpdateDTO();
        updateDTO.setNickname("New Nickname");
        
        // 模拟Mapper行为 - 用户不存在
        when(userService.getOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        
        // 验证抛出异常
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUserProfile("nonexistentuser", updateDTO);
        });
        
        assertEquals("用户不存在", exception.getMessage());
    }
} 