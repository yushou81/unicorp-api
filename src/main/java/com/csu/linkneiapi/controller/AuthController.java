package com.csu.linkneiapi.controller;

import com.csu.linkneiapi.dto.LoginDTO;
import com.csu.linkneiapi.dto.RegisterDTO;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证相关API
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "用户认证相关操作 (注册与登录)")
@Validated
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED) // 设置返回状态码为201
    @Operation(summary = "新用户注册", description = "创建一个新用户，并同时为其生成一个空的个人档案")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "注册成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "用户名已存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "422", description = "输入数据验证失败",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<?> register(
            @Parameter(description = "用户注册信息", required = true) 
            @Valid @RequestBody RegisterDTO registerDTO) {
        try {
            userService.registerWithProfile(registerDTO);
            return new ResultVO<>(201, "注册成功！", null);
        } catch (RuntimeException e) {
            return ResultVO.error(400, e.getMessage());
        }
    }
    
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用用户名和密码进行认证，成功后返回JWT令牌")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登录成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "认证失败 (用户名或密码错误)",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "422", description = "输入数据验证失败",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<?> login(
            @Parameter(description = "用户登录信息", required = true) 
            @Valid @RequestBody LoginDTO loginDTO) {
        try {
            JwtResponseVO jwtResponse = userService.login(loginDTO);
            return ResultVO.success(jwtResponse);
        } catch (RuntimeException e) {
            return ResultVO.error(401, e.getMessage());
        }
    }
} 