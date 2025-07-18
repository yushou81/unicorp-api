package com.csu.unicorp.controller;

import com.csu.unicorp.common.annotation.Log;
import com.csu.unicorp.common.constants.LogActionType;
import com.csu.unicorp.dto.EnterpriseRegistrationDTO;
import com.csu.unicorp.dto.LoginCredentialsDTO;
import com.csu.unicorp.dto.RefreshTokenDTO;
import com.csu.unicorp.dto.StudentRegistrationDTO;
import com.csu.unicorp.dto.UserProfileUpdateDTO;
import com.csu.unicorp.dto.PasswordUpdateDTO;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.TokenVO;
import com.csu.unicorp.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;

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
    
    @Value("${server.port:8081}")
    private int serverPort;
    
    @Value("${server.external-ip:#{null}}")
    private String externalIp;
    
    /**
     * 用户登录
     */
    @Operation(summary = "通用登录接口", description = "用户使用账号和密码登录，成功后返回JWT、用户昵称和角色信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登录成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "认证失败")
    })
    @PostMapping("/login")
    @Log(value = LogActionType.LOGIN, module = "用户认证")
    public ResultVO<TokenVO> login(@Valid @RequestBody LoginCredentialsDTO loginDto) {
        TokenVO tokenResponse = userService.login(loginDto);
        return ResultVO.success("登录成功", tokenResponse);
    }
    
    /**
     * 用户登出
     */
    @Operation(summary = "用户登出接口", description = "用户登出系统，使当前令牌失效")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登出成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @PostMapping("/logout")
    @Log(value = LogActionType.LOGOUT, module = "用户认证")
    public ResultVO<Void> logout(
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // 提取JWT令牌（去除"Bearer "前缀）
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            userService.logout(token, userDetails);
            return ResultVO.success("登出成功");
        }
        
        return ResultVO.error("无效的令牌");
    }
    
    /**
     * 刷新令牌
     */
    @Operation(summary = "刷新令牌接口", description = "使用刷新令牌获取新的访问令牌")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "令牌刷新成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "无效的刷新令牌"),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @PostMapping("/refresh")
    public ResultVO<TokenVO> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        TokenVO tokenResponse = userService.refreshToken(refreshTokenDTO);
        return ResultVO.success("令牌刷新成功", tokenResponse);
    }
    
    /**
     * 学生注册
     */
    @Operation(summary = "学生注册接口", description = "学生选择已存在的学校进行注册，并提供实名信息和邮箱验证码")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "学生注册成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "无效的输入，账号/邮箱已存在，或验证码错误")
    })
    @PostMapping("/register/student")
    @Log(value = LogActionType.REGISTER, module = "用户管理")
    public ResultVO<UserVO> registerStudent(@Valid @RequestBody StudentRegistrationDTO registrationDto) {
        UserVO user = userService.registerStudent(registrationDto, true); // 启用验证码检查
        return ResultVO.success("注册成功", user);
    }
    
    /**
     * 企业注册
     */
    @Operation(summary = "企业注册接口", description = "企业代表进行公开注册。注册后，企业和其管理员账号状态均为'pending'，需要系统管理员审核。需要提供邮箱验证码。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "注册申请已提交，等待审核", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "无效的输入，邮箱/手机号/企业名称已存在，或验证码错误")
    })
    @PostMapping(value = "/register/enterprise", consumes = "multipart/form-data")
    @Log(value = LogActionType.REGISTER, module = "用户管理")
    public ResultVO<UserVO> registerEnterprise(
            @RequestParam @NotBlank(message = "企业名称不能为空") String organizationName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String website,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String companySize,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) String adminNickname,
            @RequestParam @NotBlank(message = "管理员密码不能为空") String adminPassword,
            @RequestParam @NotBlank(message = "管理员邮箱不能为空") @Email(message = "管理员邮箱格式不正确") String adminEmail,
            @RequestParam @NotBlank(message = "邮箱验证码不能为空") String emailVerificationCode,
            @RequestParam(required = false) @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String adminPhone,
            @RequestPart("logo") MultipartFile logo,
            @RequestPart("businessLicense") MultipartFile businessLicense) {
        
        // 组装EnterpriseRegistrationDTO对象
        EnterpriseRegistrationDTO registrationDto = new EnterpriseRegistrationDTO();
        registrationDto.setOrganizationName(organizationName);
        registrationDto.setDescription(description);
        registrationDto.setAddress(address);
        registrationDto.setWebsite(website);
        registrationDto.setIndustry(industry);
        registrationDto.setCompanySize(companySize);
        registrationDto.setLatitude(latitude);
        registrationDto.setLongitude(longitude);
        registrationDto.setAdminNickname(adminNickname);
        registrationDto.setAdminPassword(adminPassword);
        registrationDto.setAdminEmail(adminEmail);
        registrationDto.setEmailVerificationCode(emailVerificationCode);
        registrationDto.setAdminPhone(adminPhone);
        
        UserVO user = userService.registerEnterprise(registrationDto, logo, businessLicense, true); // 启用验证码检查
        return ResultVO.success("企业注册申请已提交，等待审核", user);
    }
    
    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前登录用户信息", description = "根据提供的JWT获取当前登录用户的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户信息", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @GetMapping("/me")
    public ResultVO<UserVO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // 输出用户角色信息，用于调试
        log.info("当前用户: {}, 角色: {}", userDetails.getUsername(), 
                userDetails.getAuthorities().stream()
                        .map(auth -> auth.getAuthority())
                        .collect(Collectors.joining(", ")));
        
        UserVO user = userService.getCurrentUser(userDetails);
        return ResultVO.success("获取用户信息成功", user);
    }
    
    /**
     * 更新用户个人信息
     */
    @Operation(summary = "更新用户个人信息", description = "允许用户修改自己的邮箱、手机号和昵称")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "信息更新成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "无效的输入，或邮箱/手机号已被其他用户使用"),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @PutMapping("/profile")
    @Log(value = LogActionType.UPDATE_PROFILE, module = "用户管理")
    public ResultVO<UserVO> updateUserProfile(
            @Valid @RequestBody UserProfileUpdateDTO profileUpdateDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserVO updatedUser = userService.updateUserProfile(profileUpdateDTO, userDetails);
        return ResultVO.success("个人信息更新成功", updatedUser);
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
    @Log(value = LogActionType.CHANGE_PASSWORD, module = "用户管理")
    public ResultVO<Void> updatePassword(
            @Valid @RequestBody PasswordUpdateDTO passwordUpdateDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.updatePassword(passwordUpdateDTO, userDetails);
        return ResultVO.success("密码修改成功");
    }
    
    /**
     * 上传头像
     */
    @Operation(summary = "上传用户头像", description = "允许用户上传自己的头像")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "头像上传成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "无效的文件格式或大小"),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @PostMapping(value = "/avatar", consumes = "multipart/form-data")
    public ResultVO<UserVO> updateAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserVO updatedUser = userService.updateAvatar(file, userDetails);
        return ResultVO.success("头像上传成功", updatedUser);
    }
    
    /**
     * 获取GitHub登录URL
     */
    @Operation(summary = "获取GitHub登录URL", description = "获取GitHub OAuth2登录的URL")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取GitHub登录URL", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/github/login-url")
    public ResultVO<String> getGitHubLoginUrl(HttpServletRequest request) {

        String host = externalIp;
        
        // 获取当前服务器的地址和端口
        String serverAddress = request.getScheme() + "://" + host;
        if (serverPort != 80 && serverPort != 443) {
            serverAddress += ":" + serverPort;
        }
        
        // 构建完整的GitHub登录URL
        String fullLoginUrl = serverAddress + "/api/v1/auth/oauth2/authorization/github";
        
        log.info("构建GitHub登录URL: {}", fullLoginUrl);
        return ResultVO.success("获取GitHub登录URL成功", fullLoginUrl);
    }
    
    /**
     * 搜索用户
     */
    @Operation(summary = "搜索用户", description = "通过电话号码或邮箱搜索用户")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查找用户成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "未找到匹配的用户"),
        @ApiResponse(responseCode = "401", description = "未授权")
    })
    @GetMapping("/search")
    public ResultVO<UserVO> searchUser(
            @RequestParam @Parameter(description = "搜索关键词（电话号码或邮箱）") String keyword,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        // 用户需要登录才能搜索
        if (userDetails == null) {
            return ResultVO.error("请先登录");
        }
        
        // 调用service进行搜索
        try {
            UserVO user = userService.searchUserByPhoneOrEmail(keyword);
            return ResultVO.success("查找用户成功", user);
        } catch (Exception e) {
            return ResultVO.error(e.getMessage());
        }
    }
} 