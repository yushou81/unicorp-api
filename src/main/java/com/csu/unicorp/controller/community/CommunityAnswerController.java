package com.csu.unicorp.controller.community;

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
import com.csu.unicorp.dto.community.AnswerDTO;
import com.csu.unicorp.service.CommunityAnswerService;
import com.csu.unicorp.service.CommunityQuestionService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.community.AnswerVO;

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
 * 社区问题回答Controller
 */
@RestController
@RequestMapping("/v1/community/answers")
@RequiredArgsConstructor
@Tag(name = "社区回答API", description = "社区问题回答相关接口")
public class CommunityAnswerController {
    
    private final CommunityAnswerService answerService;
    private final CommunityQuestionService questionService;
    
    /**
     * 创建回答
     * @param userDetails 当前登录用户
     * @param answerDTO 回答DTO
     * @return 回答ID
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "创建回答", description = "创建问题的回答，需要登录")
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
        @ApiResponse(responseCode = "404", description = "问题不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Long> createAnswer(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid @Parameter(description = "回答信息") AnswerDTO answerDTO) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        // 检查问题是否存在
        if (!questionService.existsById(answerDTO.getQuestionId())) {
            return ResultVO.error(404, "问题不存在");
        }
        
        Long answerId = answerService.createAnswer(userId, answerDTO);
        return ResultVO.success("创建回答成功", answerId);
    }
    
    /**
     * 获取回答详情
     * @param answerId 回答ID
     * @param userDetails 当前登录用户
     * @return 回答详情
     */
    @GetMapping("/{answerId}")
    @Operation(summary = "获取回答详情", description = "根据回答ID获取回答详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "回答不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<AnswerVO> getAnswerDetail(
            @PathVariable @Parameter(description = "回答ID") Long answerId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        AnswerVO answerVO = answerService.getAnswerDetail(answerId, userId);
        if (answerVO == null) {
            return ResultVO.error(404, "回答不存在");
        }
        
        return ResultVO.success("获取回答详情成功", answerVO);
    }
    
    /**
     * 更新回答
     * @param answerId 回答ID
     * @param answerDTO 回答DTO
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @PutMapping("/{answerId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "更新回答", description = "更新回答信息，需要是回答作者或管理员")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权更新该回答",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "回答不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> updateAnswer(
            @PathVariable @Parameter(description = "回答ID") Long answerId,
            @RequestBody @Valid @Parameter(description = "回答信息") AnswerDTO answerDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        // 检查用户是否有权限更新该回答
        if (!answerService.checkAnswerEditPermission(userId, answerId)) {
            return ResultVO.error(403, "无权更新该回答");
        }
        
        boolean success = answerService.updateAnswer(userId, answerId, answerDTO);
        if (!success) {
            return ResultVO.error(404, "回答不存在");
        }
        
        return ResultVO.success("更新回答成功");
    }
    
    /**
     * 删除回答
     * @param answerId 回答ID
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @DeleteMapping("/{answerId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "删除回答", description = "删除回答，需要是回答作者或管理员")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权删除该回答",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "回答不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> deleteAnswer(
            @PathVariable @Parameter(description = "回答ID") Long answerId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        // 检查用户是否有权限删除该回答
        if (!answerService.checkAnswerEditPermission(userId, answerId)) {
            return ResultVO.error(403, "无权删除该回答");
        }
        
        boolean success = answerService.deleteAnswer(userId, answerId);
        if (!success) {
            return ResultVO.error(404, "回答不存在");
        }
        
        return ResultVO.success("删除回答成功");
    }
    
    /**
     * 获取问题的回答列表
     * @param questionId 问题ID
     * @param page 页码
     * @param size 每页大小
     * @param sort 排序方式
     * @param userDetails 当前登录用户
     * @return 回答列表
     */
    @GetMapping("/question/{questionId}")
    @Operation(summary = "获取问题的回答列表", description = "获取指定问题的回答列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "问题不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<AnswerVO>> getAnswersByQuestion(
            @PathVariable @Parameter(description = "问题ID") Long questionId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @RequestParam(defaultValue = "latest") @Parameter(description = "排序方式: latest, hot") String sort,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        // 检查问题是否存在
        if (!questionService.existsById(questionId)) {
            return ResultVO.error(404, "问题不存在");
        }
        
        Page<AnswerVO> answerPage = answerService.getAnswersByQuestion(questionId, page, size, userId);
        return ResultVO.success("获取回答列表成功", answerPage);
    }
    
    /**
     * 获取用户的回答列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 回答列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户的回答列表", description = "获取指定用户的回答列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<AnswerVO>> getUserAnswers(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long currentUserId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        Page<AnswerVO> answerPage = answerService.getUserAnswers(userId, page, size, currentUserId);
        return ResultVO.success("获取用户回答列表成功", answerPage);
    }
} 