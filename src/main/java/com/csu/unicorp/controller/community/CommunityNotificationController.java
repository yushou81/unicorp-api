package com.csu.unicorp.controller.community;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.service.CommunityNotificationService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.community.NotificationVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 社区通知Controller
 */
@RestController
@RequestMapping("/v1/community/notifications")
@RequiredArgsConstructor
@Tag(name = "社区通知API", description = "社区通知相关接口")
public class CommunityNotificationController {
    
    private final CommunityNotificationService notificationService;
    
    /**
     * 获取用户通知列表
     * @param page 页码
     * @param size 每页大小
     * @param type 通知类型
     * @param isRead 是否已读
     * @param userDetails 当前登录用户
     * @return 通知列表
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取用户通知列表", description = "获取当前登录用户的通知列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<NotificationVO>> getNotifications(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @RequestParam(required = false) @Parameter(description = "通知类型") String type,
            @RequestParam(required = false) @Parameter(description = "是否已读") Boolean isRead,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        Page<NotificationVO> notificationPage = notificationService.getUserNotifications(userId, page, size, type, isRead);
        return ResultVO.success("获取通知列表成功", notificationPage);
    }
    
    /**
     * 获取通知详情
     * @param notificationId 通知ID
     * @param userDetails 当前登录用户
     * @return 通知详情
     */
    @GetMapping("/{notificationId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取通知详情", description = "获取指定通知的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权访问该通知",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "通知不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<NotificationVO> getNotificationDetail(
            @PathVariable @Parameter(description = "通知ID") Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        NotificationVO notificationVO = notificationService.getNotificationDetail(notificationId, userId);
        if (notificationVO == null) {
            return ResultVO.error(404, "通知不存在");
        }
        
        // 如果通知未读，标记为已读
        if (!notificationVO.getIsRead()) {
            notificationService.markAsRead(notificationId);
        }
        
        return ResultVO.success("获取通知详情成功", notificationVO);
    }
    
    /**
     * 标记通知为已读
     * @param notificationId 通知ID
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "标记通知为已读", description = "标记指定通知为已读状态")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "标记成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权操作该通知",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "通知不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> markNotificationAsRead(
            @PathVariable @Parameter(description = "通知ID") Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        boolean success = notificationService.markAsReadWithPermissionCheck(notificationId, userId);
        if (!success) {
            return ResultVO.error(404, "通知不存在或无权操作");
        }
        
        return ResultVO.success("标记为已读成功");
    }
    
    /**
     * 标记所有通知为已读
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "标记所有通知为已读", description = "标记用户所有通知为已读状态")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "标记成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> markAllNotificationsAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        notificationService.markAllAsRead(userId);
        return ResultVO.success("标记所有通知为已读成功");
    }
    
    /**
     * 删除通知
     * @param notificationId 通知ID
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "删除通知", description = "删除指定的通知")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权删除该通知",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "通知不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> deleteNotification(
            @PathVariable @Parameter(description = "通知ID") Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        boolean success = notificationService.deleteNotification(notificationId, userId);
        if (!success) {
            return ResultVO.error(404, "通知不存在或无权删除");
        }
        
        return ResultVO.success("删除通知成功");
    }
    
    /**
     * 删除所有通知
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @DeleteMapping("/delete-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "删除所有通知", description = "删除用户所有通知")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> deleteAllNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        notificationService.deleteAllNotifications(userId);
        return ResultVO.success("删除所有通知成功");
    }
    
    /**
     * 获取未读通知数量
     * @param userDetails 当前登录用户
     * @return 未读通知数量
     */
    @GetMapping("/unread-count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取未读通知数量", description = "获取当前登录用户的未读通知数量")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Integer> getUnreadNotificationCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        Integer unreadCount = notificationService.getUnreadCount(userId);
        return ResultVO.success("获取未读通知数量成功", unreadCount);
    }
} 