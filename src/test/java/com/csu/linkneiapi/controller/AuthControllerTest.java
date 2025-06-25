package com.csu.linkneiapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.csu.linkneiapi.dto.LoginDTO;
import com.csu.linkneiapi.dto.RegisterDTO;
import com.csu.linkneiapi.service.UserService;
import com.csu.linkneiapi.vo.JwtResponseVO;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("认证控制器集成测试")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private RegisterDTO registerDTO;
    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        // 初始化注册DTO
        registerDTO = new RegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setPassword("password123");
        registerDTO.setNickname("Test User");

        // 初始化登录DTO
        loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");
    }

    @Test
    @DisplayName("用户注册成功测试")
    void testRegisterSuccess() throws Exception {
        // 模拟Service方法不抛出异常
        doNothing().when(userService).registerWithProfile(any(RegisterDTO.class));

        // 执行注册请求并验证结果
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("注册成功"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("用户名已存在时的注册测试")
    void testRegisterWithExistingUsername() throws Exception {
        // 模拟Service方法抛出异常
        doThrow(new RuntimeException("用户名已存在")).when(userService).registerWithProfile(any(RegisterDTO.class));

        // 执行注册请求并验证结果
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.msg").value("用户名已存在"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("用户登录成功测试")
    void testLoginSuccess() throws Exception {
        // 模拟登录成功返回JWT
        JwtResponseVO jwtResponse = new JwtResponseVO("test.jwt.token", "Bearer", "testuser");
        when(userService.login(any(LoginDTO.class))).thenReturn(jwtResponse);

        // 执行登录请求并验证结果
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("操作成功"))
                .andExpect(jsonPath("$.data.token").value("test.jwt.token"))
                .andExpect(jsonPath("$.data.type").value("Bearer"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @DisplayName("用户名或密码错误的登录测试")
    void testLoginWithInvalidCredentials() throws Exception {
        // 模拟登录失败抛出异常
        when(userService.login(any(LoginDTO.class))).thenThrow(new RuntimeException("用户名或密码错误"));

        // 执行登录请求并验证结果
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.msg").value("用户名或密码错误"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("无效请求体的注册测试")
    void testRegisterWithInvalidBody() throws Exception {
        // 创建一个无效的注册DTO（空用户名）
        RegisterDTO invalidDTO = new RegisterDTO();
        invalidDTO.setPassword("password123");

        // 执行注册请求并验证结果
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isUnprocessableEntity());
    }
} 