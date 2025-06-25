package com.csu.linkneiapi.controller;

import com.csu.linkneiapi.dto.LoginDTO;
import com.csu.linkneiapi.dto.UserDTO;
import com.csu.linkneiapi.service.UserService;
import com.csu.linkneiapi.vo.JwtResponseVO;
import com.csu.linkneiapi.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user") // 结合yml中的context-path，完整路径是 /api/user
@Tag(name = "用户管理", description = "用户注册、登录和信息管理相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register") // 完整路径是 /api/user/register
    @Operation(summary = "用户注册", description = "注册新用户，用户名和手机号不能重复")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "注册成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "用户名或手机号已存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<?> register(
            @Parameter(description = "用户注册信息", required = true) 
            @RequestBody UserDTO userDTO) {
        try {
            userService.register(userDTO);
            // 注册成功，返回成功信息
            return ResultVO.success("注册成功！");
        } catch (RuntimeException e) {
            // 捕获Service层抛出的异常（如用户名或手机号已存在）
            // 返回失败信息
            return ResultVO.error(400, e.getMessage());
        }
    }
    
    @PostMapping("/login") // 完整路径是 /api/user/login
    @Operation(summary = "用户登录", description = "验证用户身份并返回JWT令牌，支持用户名或手机号登录")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登录成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "用户名/手机号或密码错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<?> login(
            @Parameter(description = "用户登录信息", required = true) 
            @RequestBody LoginDTO loginDTO) {
        try {
            // 调用Service进行登录验证，成功则返回JWT令牌
            JwtResponseVO jwtResponse = userService.login(loginDTO);
            return ResultVO.success(jwtResponse);
        } catch (RuntimeException e) {
            // 登录失败，返回错误信息
            return ResultVO.error(401, e.getMessage());
        }
    }
    
    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的基本信息")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户信息",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未登录或Token已过期",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<?> getUserInfo() {
        // 获取当前认证的用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // 构建简单的用户信息响应
        return ResultVO.success("当前登录用户: " + username);
    }
}
