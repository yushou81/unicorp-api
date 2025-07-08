package com.csu.unicorp.controller.community;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.service.CommunityAnswerService;
import com.csu.unicorp.service.CommunityFavoriteService;
import com.csu.unicorp.service.CommunityLikeService;
import com.csu.unicorp.service.CommunityQuestionService;
import com.csu.unicorp.service.CommunityTopicService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.community.AnswerVO;
import com.csu.unicorp.vo.community.QuestionVO;
import com.csu.unicorp.vo.community.TopicVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 社区交互Controller
 */
@RestController
@RequestMapping("/v1/community/interactions")
@RequiredArgsConstructor
@Tag(name = "社区交互API", description = "社区点赞、收藏相关接口")
public class CommunityInteractionController {
    
    private final CommunityLikeService likeService;
    private final CommunityFavoriteService favoriteService;
    private final CommunityQuestionService questionService;
    private final CommunityAnswerService answerService;
    private final CommunityTopicService topicService;
    
    /**
     * 点赞
     * @param targetId 目标ID
     * @param targetType 目标类型（topic, question, answer, comment）
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @PostMapping("/likes")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "点赞", description = "对话题、问题、回答或评论点赞")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "点赞成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "目标不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<?> like(
            @RequestParam @Parameter(description = "目标ID") Long targetId,
            @RequestParam @Parameter(description = "目标类型（topic, answer, comment）") String targetType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        // 验证目标类型
        if (!isValidTargetType(targetType)) {
            return ResultVO.error(400, "无效的目标类型");
        }
        
        boolean success = likeService.like(userId, targetId, targetType);
        if (!success) {
            return ResultVO.error(404, "目标不存在");
        }
        
        // 根据目标类型返回不同的结果
        if ("topic".equals(targetType)) {
            // 获取更新后的话题信息
            TopicVO topicVO = topicService.getTopicDetail(targetId, userId);
            return ResultVO.success("点赞成功", topicVO);
        } else if ("question".equals(targetType)) {
            // 获取更新后的问题信息
            QuestionVO questionVO = questionService.getQuestionDetail(targetId, userId);
            return ResultVO.success("点赞成功", questionVO);
        } else if ("answer".equals(targetType)) {
            // 获取更新后的回答信息
            AnswerVO answerVO = answerService.getAnswerDetail(targetId, userId);
            return ResultVO.success("点赞成功", answerVO);
        } else {
            // 评论或其他类型，返回简单结果
            return ResultVO.success("点赞成功");
        }
    }
    
    /**
     * 取消点赞
     * @param targetId 目标ID
     * @param targetType 目标类型（topic, question, answer, comment）
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @DeleteMapping("/likes")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "取消点赞", description = "取消对话题、问题、回答或评论的点赞")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "取消点赞成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<?> unlike(
            @RequestParam @Parameter(description = "目标ID") Long targetId,
            @RequestParam @Parameter(description = "目标类型（topic, question, answer, comment）") String targetType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        // 验证目标类型
        if (!isValidTargetType(targetType)) {
            return ResultVO.error(400, "无效的目标类型");
        }
        
        likeService.unlike(userId, targetId, targetType);
        
        // 根据目标类型返回不同的结果
        if ("topic".equals(targetType)) {
            // 获取更新后的话题信息
            TopicVO topicVO = topicService.getTopicDetail(targetId, userId);
            return ResultVO.success("取消点赞成功", topicVO);
        } else if ("question".equals(targetType)) {
            // 获取更新后的问题信息
            QuestionVO questionVO = questionService.getQuestionDetail(targetId, userId);
            return ResultVO.success("取消点赞成功", questionVO);
        } else if ("answer".equals(targetType)) {
            // 获取更新后的回答信息
            AnswerVO answerVO = answerService.getAnswerDetail(targetId, userId);
            return ResultVO.success("取消点赞成功", answerVO);
        } else {
            // 评论或其他类型，返回简单结果
            return ResultVO.success("取消点赞成功");
        }
    }
    
    /**
     * 收藏
     * @param targetId 目标ID
     * @param targetType 目标类型（topic, question）
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @PostMapping("/favorites")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "收藏", description = "收藏话题或问题")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "收藏成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "目标不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<?> favorite(
            @RequestParam @Parameter(description = "目标ID") Long targetId,
            @RequestParam @Parameter(description = "目标类型（topic, question）") String targetType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        // 验证目标类型
        if (!isValidFavoriteTargetType(targetType)) {
            return ResultVO.error(400, "无效的目标类型，只能收藏话题或问题");
        }
        
        boolean success = favoriteService.favorite(userId, targetId, targetType);
        if (!success) {
            return ResultVO.error(404, "目标不存在");
        }
        
        // 根据目标类型返回不同的结果
        if ("topic".equals(targetType)) {
            // 获取更新后的话题信息
            TopicVO topicVO = topicService.getTopicDetail(targetId, userId);
            return ResultVO.success("收藏成功", topicVO);
        } else if ("question".equals(targetType)) {
            // 获取更新后的问题信息
            QuestionVO questionVO = questionService.getQuestionDetail(targetId, userId);
            return ResultVO.success("收藏成功", questionVO);
        } else {
            return ResultVO.success("收藏成功");
        }
    }
    
    /**
     * 取消收藏
     * @param targetId 目标ID
     * @param targetType 目标类型（topic, question）
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @DeleteMapping("/favorites")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "取消收藏", description = "取消收藏话题或问题")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "取消收藏成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<?> unfavorite(
            @RequestParam @Parameter(description = "目标ID") Long targetId,
            @RequestParam @Parameter(description = "目标类型（topic, question）") String targetType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        // 验证目标类型
        if (!isValidFavoriteTargetType(targetType)) {
            return ResultVO.error(400, "无效的目标类型，只能收藏话题或问题");
        }
        
        favoriteService.unfavorite(userId, targetId, targetType);
        
        // 根据目标类型返回不同的结果
        if ("topic".equals(targetType)) {
            // 获取更新后的话题信息
            TopicVO topicVO = topicService.getTopicDetail(targetId, userId);
            return ResultVO.success("取消收藏成功", topicVO);
        } else if ("question".equals(targetType)) {
            // 获取更新后的问题信息
            QuestionVO questionVO = questionService.getQuestionDetail(targetId, userId);
            return ResultVO.success("取消收藏成功", questionVO);
        } else {
            return ResultVO.success("取消收藏成功");
        }
    }
    
    /**
     * 获取用户收藏的话题列表
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 话题列表
     */
    @GetMapping("/favorites/topics")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取用户收藏的话题列表", description = "获取当前登录用户收藏的话题列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<TopicVO>> getFavoriteTopics(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        Page<TopicVO> topicPage = favoriteService.getFavoriteTopics(userId, page, size);
        return ResultVO.success("获取收藏话题成功", topicPage);
    }
    
    /**
     * 获取用户收藏的问题列表
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 问题列表
     */
    @GetMapping("/favorites/questions")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取用户收藏的问题列表", description = "获取当前登录用户收藏的问题列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<QuestionVO>> getFavoriteQuestions(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        Page<QuestionVO> questionPage = favoriteService.getFavoriteQuestions(userId, page, size);
        return ResultVO.success("获取收藏问题成功", questionPage);
    }
    
    /**
     * 获取用户点赞的回答列表
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 回答列表
     */
    @GetMapping("/likes/answers")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "获取用户点赞的回答列表", description = "获取当前登录用户点赞的回答列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<AnswerVO>> getLikedAnswers(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        Page<AnswerVO> answerPage = likeService.getLikedAnswers(userId, page, size);
        return ResultVO.success("获取点赞回答成功", answerPage);
    }
    
    /**
     * 检查用户是否点赞
     * @param targetId 目标ID
     * @param targetType 目标类型（topic, question, answer, comment）
     * @param userDetails 当前登录用户
     * @return 是否已点赞
     */
    @GetMapping("/likes/check")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "检查用户是否点赞", description = "检查当前登录用户是否对指定目标点赞")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "检查成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> checkLike(
            @RequestParam @Parameter(description = "目标ID") Long targetId,
            @RequestParam @Parameter(description = "目标类型（topic, question, answer, comment）") String targetType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        // 验证目标类型
        if (!isValidTargetType(targetType)) {
            return ResultVO.error(400, "无效的目标类型");
        }
        
        boolean isLiked = likeService.checkLike(userId, targetId, targetType);
        return ResultVO.success("检查点赞状态成功", isLiked);
    }
    
    /**
     * 检查用户是否收藏
     * @param targetId 目标ID
     * @param targetType 目标类型（topic, question）
     * @param userDetails 当前登录用户
     * @return 是否已收藏
     */
    @GetMapping("/favorites/check")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "检查用户是否收藏", description = "检查当前登录用户是否收藏指定目标")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "检查成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> checkFavorite(
            @RequestParam @Parameter(description = "目标ID") Long targetId,
            @RequestParam @Parameter(description = "目标类型（topic, question）") String targetType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        // 验证目标类型
        if (!isValidFavoriteTargetType(targetType)) {
            return ResultVO.error(400, "无效的目标类型，只能收藏话题或问题");
        }
        
        boolean isFavorited = favoriteService.checkFavorite(userId, targetId, targetType);
        return ResultVO.success("检查收藏状态成功", isFavorited);
    }
    
    /**
     * 校验目标类型是否有效
     * @param targetType 目标类型
     * @return 是否有效
     */
    private boolean isValidTargetType(String targetType) {
        return "topic".equals(targetType) || 
               "question".equals(targetType) || 
               "answer".equals(targetType) || 
               "comment".equals(targetType);
    }
    
    /**
     * 校验收藏目标类型是否有效
     * @param targetType 目标类型
     * @return 是否有效
     */
    private boolean isValidFavoriteTargetType(String targetType) {
        return "topic".equals(targetType) || 
               "question".equals(targetType);
    }
} 