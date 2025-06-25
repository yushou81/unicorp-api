package com.csu.linkneiapi.controller;

import com.csu.linkneiapi.dto.ProfileUpdateDTO;
import com.csu.linkneiapi.service.UserService;
import com.csu.linkneiapi.vo.ResultVO;
import com.csu.linkneiapi.vo.UserProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * 用户个人资料管理控制器
 */
@RestController
@RequestMapping("/user")
@Tag(name = "User Profile", description = "用户个人资料管理")
@SecurityRequirement(name = "BearerAuth")
public class UserProfileController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户的个人资料
     */
    @GetMapping("/profile")
    @Operation(summary = "获取当前登录用户的个人资料", description = "根据请求头中携带的JWT Token，获取并返回当前用户的详细个人信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取个人资料", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "401", description = "未授权", 
                    content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<UserProfileVO> getUserProfile(Principal principal) {
        // 从Principal中获取当前登录用户的用户名
        String username = principal.getName();
        // 调用Service层方法获取用户个人资料
        UserProfileVO profileVO = userService.getUserProfile(username);
        return ResultVO.success(profileVO);
    }

    /**
     * 更新当前登录用户的个人资料
     */
    @PutMapping("/profile")
    @Operation(summary = "更新当前登录用户的个人资料", description = "根据请求头中携带的JWT Token，更新当前用户的个人信息（如昵称、头像等）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "401", description = "未授权",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Void> updateUserProfile(@Valid @RequestBody ProfileUpdateDTO profileUpdateDTO, Principal principal) {
        // 从Principal中获取当前登录用户的用户名
        String username = principal.getName();
        // 调用Service层方法更新用户个人资料
        userService.updateUserProfile(username, profileUpdateDTO);
        return ResultVO.success();
    }
} 