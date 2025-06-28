package com.csu.unicorp.controller;

import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.AvatarUpdateDTO;
import com.csu.unicorp.dto.ResumeUpdateDTO;
import com.csu.unicorp.service.ProfileService;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * 用户个人资料控制器
 */
@Tag(name = "Me", description = "与当前登录用户相关的功能")
@RestController
@RequestMapping("/v1/me/profile")
@RequiredArgsConstructor
public class ProfileController {
    
    private final ProfileService profileService;
    
    /**
     * 上传/更新用户头像
     */
    @Operation(summary = "上传/更新我的头像", description = "为当前登录用户设置或更新头像。前端需先调用/files/upload获取URL。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "头像更新成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "无效的URL",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/avatar")
    public ResponseEntity<ResultVO<Void>> updateAvatar(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AvatarUpdateDTO avatarUpdateDTO) {
        
//        profileService.updateAvatar(userDetails.getId(), avatarUpdateDTO.getAvatarUrl());
        return ResponseEntity.ok(ResultVO.success("头像更新成功"));
    }
    
    /**
     * 上传/更新学生简历
     */
    @Operation(summary = "上传/更新我的简历", description = "为当前登录的学生用户设置或更新简历文件。前端需先调用/files/upload获取URL。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "简历更新成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "无效的URL",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足 (非学生用户)",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/resume")
    public ResponseEntity<ResultVO<Void>> updateResume(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ResumeUpdateDTO resumeUpdateDTO) {
        
//        profileService.updateResume(userDetails.getId(), resumeUpdateDTO.getResumeUrl());
        return ResponseEntity.ok(ResultVO.success("简历更新成功"));
    }
} 