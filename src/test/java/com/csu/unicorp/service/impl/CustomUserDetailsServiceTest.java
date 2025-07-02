package com.csu.unicorp.service.impl;

import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.entity.user.User;
import com.csu.unicorp.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class CustomUserDetailsServiceTest {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private UserMapper userMapper;

    private User testUser;

    @BeforeEach
    public void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setId(1);
        testUser.setAccount("testuser");
        testUser.setPassword("password");
        testUser.setNickname("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setStatus("active");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        testUser.setIsDeleted(false);

        // 模拟UserMapper行为
        when(userMapper.selectByAccount("testuser")).thenReturn(testUser);
        when(userMapper.selectByAccount("nonexistent")).thenReturn(null);
        when(userMapper.selectRoleByUserId(1)).thenReturn("TEACHER");
    }

    @Test
    public void testLoadUserByUsername_Success() {
        // 调用service方法
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // 断言
        assertNotNull(userDetails, "返回的UserDetails不应为空");
        assertTrue(userDetails instanceof CustomUserDetails, "UserDetails应是CustomUserDetails类型");
        assertEquals("1", userDetails.getUsername(), "用户名应为用户ID字符串");
        assertEquals(testUser.getPassword(), userDetails.getPassword(), "密码应匹配");
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER")), "应有ROLE_TEACHER角色");
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // 断言抛出UsernameNotFoundException异常
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent");
        }, "应该抛出UsernameNotFoundException异常");
    }
} 