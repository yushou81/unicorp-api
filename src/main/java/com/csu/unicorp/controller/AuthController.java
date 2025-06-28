package com.csu.unicorp.controller;

import com.csu.unicorp.dto.EnterpriseRegistrationDTO;
import com.csu.unicorp.dto.LoginCredentialsDTO;
import com.csu.unicorp.dto.StudentRegistrationDTO;
import com.csu.unicorp.dto.UserProfileUpdateDTO;
import com.csu.unicorp.dto.PasswordUpdateDTO;
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
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 认证相关接口
 */
@Tag(name = "Authentication", description = "认证管理")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    /**
     * 用户登录
     */
    @Operation(summary = "通用登录接口", description = "用户使用账号和密码登录，成功后返回JWT、用户昵称和角色信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登录成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = TokenVO.class))),
        @ApiResponse(responseCode = "401", description = "认证失败")
    })
    @PostMapping("/login")
    public ResponseEntity<ResultVO<TokenVO>> login(@Valid @RequestBody LoginCredentialsDTO loginDto) {
        TokenVO tokenResponse = userService.login(loginDto);
        return ResponseEntity.ok(ResultVO.success("登录成功", tokenResponse));
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
     * 企业注册
     */
    @Operation(summary = "企业注册接口", description = "企业代表进行公开注册。注册后，企业和其管理员账号状态均为'pending'，需要系统管理员审核")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "注册申请已提交，等待审核", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = UserVO.class))),
        @ApiResponse(responseCode = "400", description = "无效的输入，或邮箱/手机号/企业名称已存在")
    })
    @PostMapping("/register/enterprise")
    public ResponseEntity<ResultVO<UserVO>> registerEnterprise(@Valid @RequestBody EnterpriseRegistrationDTO registrationDto) {
        UserVO user = userService.registerEnterprise(registrationDto);
        return new ResponseEntity<>(ResultVO.success("企业注册申请已提交，等待审核", user), HttpStatus.ACCEPTED);
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
        // 输出用户角色信息，用于调试
        log.info("当前用户: {}, 角色: {}", userDetails.getUsername(), 
                userDetails.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.joining(", ")));
        
        UserVO user = userService.getCurrentUser(userDetails);
        return ResponseEntity.ok(ResultVO.success("获取用户信息成功", user));
    }
    
    /**
     * 更新用户个人信息
     */
    @Operation(summary = "更新用户个人信息", description = "允许用户修改自己的邮箱、手机号和昵称")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "信息更新成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = UserVO.class))),
        @ApiResponse(responseCode = "400", description = "无效的输入，或邮箱/手机号已被其他用户使用"),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @PutMapping("/profile")
    public ResponseEntity<ResultVO<UserVO>> updateUserProfile(
            @Valid @RequestBody UserProfileUpdateDTO profileUpdateDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserVO updatedUser = userService.updateUserProfile(profileUpdateDTO, userDetails);
        return ResponseEntity.ok(ResultVO.success("个人信息更新成功", updatedUser));
    }
    
    /**
     * 修改密码
     */
    @Operation(summary = "修改用户密码", description = "允许用户修改自己的登录密码，需要提供原密码进行验证")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "密码修改成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "原密码不正确"),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @PutMapping("/password")
    public ResponseEntity<ResultVO<Void>> updatePassword(
            @Valid @RequestBody PasswordUpdateDTO passwordUpdateDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.updatePassword(passwordUpdateDTO, userDetails);
        return ResponseEntity.ok(ResultVO.success("密码修改成功"));
    }
} 