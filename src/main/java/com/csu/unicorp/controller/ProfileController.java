package com.csu.unicorp.controller;

import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.ProfileUpdateDTO;
import com.csu.unicorp.service.ProfileService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.UserProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 个人主页控制器
 */
@Tag(name = "Profiles", description = "用户个人主页与档案管理")
@RestController
@RequiredArgsConstructor
public class ProfileController {
    
    private final ProfileService profileService;
    
    /**
     * 获取指定用户的公开主页信息
     */
    @Operation(summary = "获取指定用户的公开主页信息", description = "获取一个用户的公开展示信息，返回的数据结构会根据用户角色有所不同。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户主页信息",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "用户未找到",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/v1/profiles/{userId}")
    public ResultVO<UserProfileVO> getUserProfile(@PathVariable Integer userId) {
        UserProfileVO userProfile = profileService.getUserProfile(userId);
        return ResultVO.success("获取用户主页信息成功", userProfile);
    }
    
    /**
     * 获取我自己的详细档案信息
     */
    @Operation(summary = "获取我自己的详细档案信息", description = "获取当前登录用户的完整档案信息，用于编辑个人主页页面。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取档案信息",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/v1/me/profile")
    public ResultVO<UserProfileVO> getMyProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileVO userProfile = profileService.getCurrentUserProfile(userDetails.getUser().getId());
        return ResultVO.success("获取个人档案信息成功", userProfile);
    }
    
    /**
     * 更新我自己的基本档案
     */
    @Operation(summary = "更新我自己的基本档案", description = "更新当前登录用户的基本信息，如昵称、简介、头像等。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "档案更新成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/v1/me/profile")
    public ResultVO<UserProfileVO> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProfileUpdateDTO profileUpdateDTO) {
        UserProfileVO updatedProfile = profileService.updateUserProfile(userDetails.getUser().getId(), profileUpdateDTO);
        return ResultVO.success("个人档案更新成功", updatedProfile);
    }
} 