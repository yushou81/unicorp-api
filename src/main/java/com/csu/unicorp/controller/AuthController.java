package com.csu.unicorp.controller;

import com.csu.unicorp.dto.LoginCredentialsDTO;
import com.csu.unicorp.dto.StudentRegistrationDTO;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.TokenVO;
import com.csu.unicorp.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 认证相关接口
 */
@Tag(name = "Authentication", description = "认证管理")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    
    /**
     * 用户登录
     */
    @Operation(summary = "通用登录接口", description = "用户使用账号和密码登录，成功后返回JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登录成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = TokenVO.class))),
        @ApiResponse(responseCode = "401", description = "认证失败")
    })
    @PostMapping("/login")
    public ResponseEntity<ResultVO<TokenVO>> login(@Valid @RequestBody LoginCredentialsDTO loginDto) {
        TokenVO token = userService.login(loginDto);
        return ResponseEntity.ok(ResultVO.success("登录成功", token));
    }
    
    /**
     * 学生注册
     */
    @Operation(summary = "学生注册接口", description = "学生选择已存在的学校进行注册，并提供实名信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "学生注册成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = UserVO.class))),
        @ApiResponse(responseCode = "400", description = "无效的输入，或账号/邮箱已存在")
    })
    @PostMapping("/register/student")
    public ResponseEntity<ResultVO<UserVO>> registerStudent(@Valid @RequestBody StudentRegistrationDTO registrationDto) {
        UserVO user = userService.registerStudent(registrationDto);
        return new ResponseEntity<>(ResultVO.success("注册成功", user), HttpStatus.CREATED);
    }
    
    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前登录用户信息", description = "根据提供的JWT获取当前登录用户的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户信息", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = UserVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @GetMapping("/me")
    public ResponseEntity<ResultVO<UserVO>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserVO user = userService.getCurrentUser(userDetails);
        return ResponseEntity.ok(ResultVO.success("获取用户信息成功", user));
    }
} 