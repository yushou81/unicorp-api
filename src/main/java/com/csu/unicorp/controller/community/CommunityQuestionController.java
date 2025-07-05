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
import com.csu.unicorp.dto.community.QuestionDTO;
import com.csu.unicorp.service.CommunityQuestionService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.community.QuestionVO;

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
 * 社区问题Controller
 */
@RestController
@RequestMapping("/v1/community/questions")
@RequiredArgsConstructor
@Tag(name = "社区问题API", description = "社区问题相关接口")
public class CommunityQuestionController {
    
    private final CommunityQuestionService questionService;
    
    /**
     * 创建问题
     * @param userDetails 当前登录用户
     * @param questionDTO 问题DTO
     * @return 问题ID
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "创建问题", description = "创建新的问题，需要登录")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "创建成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Long> createQuestion(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid @Parameter(description = "问题信息") QuestionDTO questionDTO) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        Long questionId = questionService.createQuestion(userId, questionDTO);
        return ResultVO.success("创建问题成功", questionId);
    }
    
    /**
     * 获取问题详情
     * @param questionId 问题ID
     * @param userDetails 当前登录用户
     * @return 问题详情
     */
    @GetMapping("/{questionId}")
    @Operation(summary = "获取问题详情", description = "根据问题ID获取问题详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "问题不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<QuestionVO> getQuestionDetail(
            @PathVariable @Parameter(description = "问题ID") Long questionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        QuestionVO questionVO = questionService.getQuestionDetail(questionId, userId);
        if (questionVO == null) {
            return ResultVO.error(404, "问题不存在");
        }
        
        // 增加浏览次数
        questionService.incrementViewCount(questionId);
        
        return ResultVO.success("获取问题详情成功", questionVO);
    }
    
    /**
     * 更新问题
     * @param questionId 问题ID
     * @param questionDTO 问题DTO
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @PutMapping("/{questionId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "更新问题", description = "更新问题信息，需要是问题作者或管理员")
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
        @ApiResponse(responseCode = "403", description = "无权更新该问题",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "问题不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> updateQuestion(
            @PathVariable @Parameter(description = "问题ID") Long questionId,
            @RequestBody @Valid @Parameter(description = "问题信息") QuestionDTO questionDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        // 检查用户是否有权限更新该问题
        if (!questionService.checkQuestionEditPermission(userId, questionId)) {
            return ResultVO.error(403, "无权更新该问题");
        }
        
        boolean success = questionService.updateQuestion(userId, questionId, questionDTO);
        if (!success) {
            return ResultVO.error(404, "问题不存在");
        }
        
        return ResultVO.success("更新问题成功");
    }
    
    /**
     * 删除问题
     * @param questionId 问题ID
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @DeleteMapping("/{questionId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "删除问题", description = "删除问题，需要是问题作者或管理员")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权删除该问题",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "问题不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> deleteQuestion(
            @PathVariable @Parameter(description = "问题ID") Long questionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        // 检查用户是否有权限删除该问题
        if (!questionService.checkQuestionEditPermission(userId, questionId)) {
            return ResultVO.error(403, "无权删除该问题");
        }
        
        boolean success = questionService.deleteQuestion(userId, questionId);
        if (!success) {
            return ResultVO.error(404, "问题不存在");
        }
        
        return ResultVO.success("删除问题成功");
    }
    
    /**
     * 获取热门问题列表
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 热门问题列表
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门问题列表", description = "获取热门问题列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<QuestionVO>> getHotQuestions(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        Page<QuestionVO> questionPage = questionService.getHotQuestions(page, size, userId);
        return ResultVO.success("获取热门问题列表成功", questionPage);
    }
    
    /**
     * 获取最新问题列表
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 最新问题列表
     */
    @GetMapping("/latest")
    @Operation(summary = "获取最新问题列表", description = "获取最新问题列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<QuestionVO>> getLatestQuestions(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        Page<QuestionVO> questionPage = questionService.getLatestQuestions(page, size, userId);
        return ResultVO.success("获取最新问题列表成功", questionPage);
    }
    
    /**
     * 搜索问题
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 搜索结果
     */
    @GetMapping("/search")
    @Operation(summary = "搜索问题", description = "根据关键词搜索问题")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "搜索成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<QuestionVO>> searchQuestions(
            @RequestParam @Parameter(description = "搜索关键词") String keyword,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        Page<QuestionVO> questionPage = questionService.searchQuestions(keyword, page, size, userId);
        return ResultVO.success("搜索问题成功", questionPage);
    }
    
    /**
     * 获取用户问题列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 用户问题列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户问题列表", description = "获取指定用户发布的问题列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<QuestionVO>> getUserQuestions(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long currentUserId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        Page<QuestionVO> questionPage = questionService.getUserQuestions(userId, page, size, currentUserId);
        return ResultVO.success("获取用户问题列表成功", questionPage);
    }
    
    /**
     * 获取未解决问题列表
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 未解决问题列表
     */
    @GetMapping("/unsolved")
    @Operation(summary = "获取未解决问题列表", description = "获取未解决的问题列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<QuestionVO>> getUnsolvedQuestions(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        Page<QuestionVO> questionPage = questionService.getUnsolvedQuestions(page, size, userId);
        return ResultVO.success("获取未解决问题列表成功", questionPage);
    }
    
    /**
     * 标记问题为已解决
     * @param questionId 问题ID
     * @param answerId 被采纳的答案ID
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @PutMapping("/{questionId}/solved")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "标记问题为已解决", description = "标记问题为已解决并采纳指定答案，需要是问题作者")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "设置成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权操作",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "问题或答案不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> markQuestionSolved(
            @PathVariable @Parameter(description = "问题ID") Long questionId,
            @RequestParam @Parameter(description = "被采纳的答案ID") Long answerId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        System.out.println("userId: " + userId);
        // 检查用户是否有权限操作该问题
        if (!questionService.isQuestionAuthor(userId, questionId)) {
            return ResultVO.error(403, "只有问题作者可以采纳答案");
        }
        
        boolean success = questionService.markQuestionSolved(questionId, answerId);
        if (!success) {
            return ResultVO.error(404, "问题或答案不存在");
        }
        
        return ResultVO.success("采纳答案成功");
    }
    
    /**
     * 获取相关问题列表
     * @param questionId 问题ID
     * @param limit 限制数量
     * @param userDetails 当前登录用户
     * @return 相关问题列表
     */
    @GetMapping("/{questionId}/related")
    @Operation(summary = "获取相关问题列表", description = "根据当前问题获取相关问题推荐")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "问题不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<QuestionVO>> getRelatedQuestions(
            @PathVariable @Parameter(description = "问题ID") Long questionId,
            @RequestParam(defaultValue = "5") @Parameter(description = "限制数量") Integer limit,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        // 检查问题是否存在
        if (!questionService.existsById(questionId)) {
            return ResultVO.error(404, "问题不存在");
        }
        
        Page<QuestionVO> questionPage = questionService.getRelatedQuestions(questionId, limit, userId);
        return ResultVO.success("获取相关问题成功", questionPage);
    }
} 