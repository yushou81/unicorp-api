package com.csu.unicorp.controller.community;

import java.util.List;
import java.util.Collections;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.community.CommentDTO;
import com.csu.unicorp.service.CommunityCommentService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.community.CommentVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 社区评论Controller
 */
@RestController
@RequestMapping("/v1/community/comments")
@RequiredArgsConstructor
@Tag(name = "社区评论API", description = "社区评论相关接口")
public class CommunityCommentController {
    
    private final CommunityCommentService commentService;
    
    /**
     * 创建评论
     * @param userDetails 当前登录用户
     * @param commentDTO 评论DTO
     * @return 评论ID
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "创建评论", description = "创建评论，需要登录")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "创建成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "评论目标不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Long> createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid @Parameter(description = "评论信息") CommentDTO commentDTO) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        Long commentId = commentService.createComment(userId, commentDTO);
        if (commentId == null) {
            return ResultVO.error(404, "评论目标不存在");
        }
        
        return ResultVO.success("创建评论成功", commentId);
    }
    
    /**
     * 删除评论
     * @param commentId 评论ID
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "删除评论", description = "删除评论，需要是评论作者或管理员")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权删除该评论",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "评论不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> deleteComment(
            @PathVariable @Parameter(description = "评论ID") Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        // 检查用户是否有权限删除该评论
        if (!commentService.checkCommentEditPermission(userId, commentId)) {
            return ResultVO.error(403, "无权删除该评论");
        }
        
        boolean success = commentService.deleteComment(userId, commentId);
        if (!success) {
            return ResultVO.error(404, "评论不存在");
        }
        
        return ResultVO.success("删除评论成功");
    }
    
    /**
     * 获取话题的评论列表
     * @param topicId 话题ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论列表
     */
    @GetMapping("/topic/{topicId}")
    @Operation(summary = "获取话题的评论列表", description = "获取指定话题的评论列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "话题不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<CommentVO>> getTopicComments(
            @PathVariable @Parameter(description = "话题ID") Long topicId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {
        
        Page<CommentVO> commentPage = commentService.getTopicComments(topicId, page, size, null);
        if (commentPage == null) {
            return ResultVO.error(404, "话题不存在");
        }
        
        return ResultVO.success("获取话题评论列表成功", commentPage);
    }
    
    /**
     * 获取回答的评论列表
     * @param answerId 回答ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论列表
     */
    @GetMapping("/answer/{answerId}")
    @Operation(summary = "获取回答的评论列表", description = "获取指定回答的评论列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "回答不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<CommentVO>> getAnswerComments(
            @PathVariable @Parameter(description = "回答ID") Long answerId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {
        
        Page<CommentVO> commentPage = commentService.getAnswerComments(answerId, page, size);
        if (commentPage == null) {
            return ResultVO.error(404, "回答不存在");
        }
        
        return ResultVO.success("获取回答评论列表成功", commentPage);
    }
    
    /**
     * 获取评论的回复列表
     * @param commentId 评论ID
     * @param page 页码
     * @param size 每页大小
     * @return 回复列表
     */
    @GetMapping("/{commentId}/replies")
    @Operation(summary = "获取评论的回复列表", description = "获取指定评论的回复列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "评论不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<CommentVO>> getCommentReplies(
            @PathVariable @Parameter(description = "评论ID") Long commentId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {
        
        List<CommentVO> replies = commentService.getCommentReplies(commentId, null);
        if (replies == null) {
            return ResultVO.error(404, "评论不存在");
        }
        
        // 手动分页
        int start = (page - 1) * size;
        int end = Math.min(start + size, replies.size());
        
        if (start >= replies.size()) {
            start = 0;
            end = Math.min(size, replies.size());
        }
        
        List<CommentVO> pageData = start < end ? replies.subList(start, end) : Collections.emptyList();
        
        Page<CommentVO> commentPage = new Page<>(page, size, replies.size());
        commentPage.setRecords(pageData);
        
        return ResultVO.success("获取评论回复列表成功", commentPage);
    }
    
    /**
     * 获取用户的评论列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户的评论列表", description = "获取指定用户的评论列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<CommentVO>> getUserComments(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {
        
        Page<CommentVO> commentPage = commentService.getUserComments(userId, page, size);
        return ResultVO.success("获取用户评论列表成功", commentPage);
    }
} 