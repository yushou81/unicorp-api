package com.csu.unicorp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.service.FavoriteService;
import com.csu.unicorp.vo.JobVO;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位收藏控制器
 */
@Tag(name = "Favorites", description = "岗位收藏管理")
@RestController
@RequiredArgsConstructor
public class FavoriteController {
    
    private final FavoriteService favoriteService;
    
    /**
     * 获取我收藏的岗位列表
     */
    @Operation(summary = "[学生] 获取我收藏的岗位列表", description = "获取当前登录学生收藏的所有招聘岗位列表，支持分页。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取收藏列表",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "用户未登录",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足 (非学生用户)",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/v1/me/favorites/jobs")
    public ResultVO<IPage<JobVO>> getFavoriteJobs(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // 检查用户是否已登录
        if (userDetails == null) {
            return ResultVO.fail(HttpStatus.UNAUTHORIZED.value(), "用户未登录");
        }
        
        IPage<JobVO> jobPage = favoriteService.getFavoriteJobs(userDetails.getUser().getId(), page, size);
        
        return ResultVO.success("获取收藏岗位列表成功", jobPage);
    }
    
    /**
     * 收藏一个岗位
     */
    @Operation(summary = "[学生] 收藏一个岗位", description = "将指定的岗位添加至当前登录学生的收藏列表。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "收藏成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "已收藏过该岗位",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "用户未登录",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足 (非学生用户)",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "岗位未找到",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/v1/jobs/{id}/favorite")
    public ResultVO<Void> favoriteJob(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Integer jobId) {
        
        // 检查用户是否已登录
        if (userDetails == null) {
            return ResultVO.fail(HttpStatus.UNAUTHORIZED.value(), "用户未登录");
        }
        
        return favoriteService.favoriteJob(userDetails.getUser().getId(), jobId);
    }
    
    /**
     * 取消收藏一个岗位
     */
    @Operation(summary = "[学生] 取消收藏一个岗位", description = "将指定的岗位从当前登录学生的收藏列表中移除。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "取消收藏成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "用户未登录",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足 (非学生用户)",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/v1/jobs/{id}/favorite")
    public ResultVO<Void> unfavoriteJob(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") Integer jobId) {
        
        // 检查用户是否已登录
        if (userDetails == null) {
            return ResultVO.fail(HttpStatus.UNAUTHORIZED.value(), "用户未登录");
        }
        
        return favoriteService.unfavoriteJob(userDetails.getUser().getId(), jobId);
    }
} 