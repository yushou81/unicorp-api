package com.csu.linkneiapi.controller;

import com.csu.linkneiapi.service.UserService;
import com.csu.linkneiapi.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user") // 结合yml中的context-path，完整路径是 /api/user
@Tag(name = "用户管理", description = "用户信息管理相关接口")
@Validated
public class UserController {

    @Autowired
    private UserService userService;
    
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
