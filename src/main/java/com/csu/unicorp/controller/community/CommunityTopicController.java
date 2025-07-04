package com.csu.unicorp.controller.community;

import java.util.List;

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
import com.csu.unicorp.dto.community.TopicDTO;
import com.csu.unicorp.service.CommunityCategoryService;
import com.csu.unicorp.service.CommunityTopicService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.community.TopicVO;

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
 * 社区话题Controller
 */
@RestController
@RequestMapping("/v1/community/topics")
@RequiredArgsConstructor
@Tag(name = "社区话题API", description = "社区话题相关接口")
public class CommunityTopicController {
    
    private final CommunityTopicService topicService;
    private final CommunityCategoryService categoryService;
    
    /**
     * 创建话题
     * @param userDetails 当前登录用户
     * @param topicDTO 话题DTO
     * @return 话题ID
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "创建话题", description = "创建新的话题，需要登录")
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
        @ApiResponse(responseCode = "403", description = "无权访问该板块",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Long> createTopic(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid @Parameter(description = "话题信息") TopicDTO topicDTO) {
        // 检查用户是否有权限发布到该板块
        Long userId = Long.valueOf(userDetails.getUser().getId());
        if (!categoryService.checkUserCategoryPermission(userId, topicDTO.getCategoryId())) {
            return ResultVO.error(403, "无权在该板块发布话题");
        }
        
        Long topicId = topicService.createTopic(userId, topicDTO);
        return ResultVO.success("创建话题成功", topicId);
    }
    
    /**
     * 获取话题详情
     * @param topicId 话题ID
     * @param userDetails 当前登录用户
     * @return 话题详情
     */
    @GetMapping("/{topicId}")
    @Operation(summary = "获取话题详情", description = "根据话题ID获取话题详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "话题不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<TopicVO> getTopicDetail(
            @PathVariable @Parameter(description = "话题ID") Long topicId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        TopicVO topicVO = topicService.getTopicDetail(topicId, userId);
        if (topicVO == null) {
            return ResultVO.error(404, "话题不存在");
        }
        
        // 增加浏览次数
        topicService.incrementViewCount(topicId);
        
        return ResultVO.success("获取话题详情成功", topicVO);
    }
    
    /**
     * 更新话题
     * @param topicId 话题ID
     * @param topicDTO 话题DTO
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @PutMapping("/{topicId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "更新话题", description = "更新话题信息，需要是话题作者或管理员")
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
        @ApiResponse(responseCode = "403", description = "无权更新该话题",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "话题不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> updateTopic(
            @PathVariable @Parameter(description = "话题ID") Long topicId,
            @RequestBody @Valid @Parameter(description = "话题信息") TopicDTO topicDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        // 检查用户是否有权限更新该话题
        if (!topicService.checkTopicEditPermission(userId, topicId)) {
            return ResultVO.error(403, "无权更新该话题");
        }
        
        // 检查用户是否有权限发布到目标板块
        if (!categoryService.checkUserCategoryPermission(userId, topicDTO.getCategoryId())) {
            return ResultVO.error(403, "无权发布到该板块");
        }
        
        boolean success = topicService.updateTopic(userId, topicId, topicDTO);
        if (!success) {
            return ResultVO.error(404, "话题不存在");
        }
        
        return ResultVO.success("更新话题成功");
    }
    
    /**
     * 删除话题
     * @param topicId 话题ID
     * @param userDetails 当前登录用户
     * @return 是否成功
     */
    @DeleteMapping("/{topicId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "删除话题", description = "删除话题，需要是话题作者或管理员")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权删除该话题",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "话题不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> deleteTopic(
            @PathVariable @Parameter(description = "话题ID") Long topicId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUser().getId());
        
        // 检查用户是否有权限删除该话题
        if (!topicService.checkTopicEditPermission(userId, topicId)) {
            return ResultVO.error(403, "无权删除该话题");
        }
        
        boolean success = topicService.deleteTopic(userId, topicId);
        if (!success) {
            return ResultVO.error(404, "话题不存在");
        }
        
        return ResultVO.success("删除话题成功");
    }
    
    /**
     * 获取板块话题列表
     * @param categoryId 板块ID
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 话题列表
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "获取板块话题列表", description = "获取指定板块的话题列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权访问该板块",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "板块不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<TopicVO>> getTopicsByCategory(
            @PathVariable @Parameter(description = "板块ID") Long categoryId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        // 检查板块是否存在
        if (categoryService.getCategoryDetail(categoryId) == null) {
            return ResultVO.error(404, "板块不存在");
        }
        
        // 检查用户是否有权限访问该板块
        if (!categoryService.checkUserCategoryPermission(userId, categoryId)) {
            return ResultVO.error(403, "无权访问该板块");
        }
        
        Page<TopicVO> topicPage = topicService.getTopicsByCategory(categoryId, page, size, userId);
        return ResultVO.success("获取板块话题列表成功", topicPage);
    }
    
    /**
     * 获取热门话题列表
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 热门话题列表
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门话题列表", description = "获取热门话题列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<TopicVO>> getHotTopics(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        Page<TopicVO> topicPage = topicService.getHotTopics(page, size, userId);
        return ResultVO.success("获取热门话题列表成功", topicPage);
    }
    
    /**
     * 获取最新话题列表
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 最新话题列表
     */
    @GetMapping("/latest")
    @Operation(summary = "获取最新话题列表", description = "获取最新话题列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<TopicVO>> getLatestTopics(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        Page<TopicVO> topicPage = topicService.getLatestTopics(page, size, userId);
        return ResultVO.success("获取最新话题列表成功", topicPage);
    }
    
    /**
     * 获取精华话题列表
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 精华话题列表
     */
    @GetMapping("/essence")
    @Operation(summary = "获取精华话题列表", description = "获取精华话题列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<TopicVO>> getEssenceTopics(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        Page<TopicVO> topicPage = topicService.getEssenceTopics(page, size, userId);
        return ResultVO.success("获取精华话题列表成功", topicPage);
    }
    
    /**
     * 搜索话题
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 搜索结果
     */
    @GetMapping("/search")
    @Operation(summary = "搜索话题", description = "根据关键词搜索话题")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "搜索成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<TopicVO>> searchTopics(
            @RequestParam @Parameter(description = "搜索关键词") String keyword,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        Page<TopicVO> topicPage = topicService.searchTopics(keyword, page, size, userId);
        return ResultVO.success("搜索话题成功", topicPage);
    }
    
    /**
     * 获取用户话题列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 用户话题列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户话题列表", description = "获取指定用户发布的话题列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<TopicVO>> getUserTopics(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long currentUserId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        Page<TopicVO> topicPage = topicService.getUserTopics(userId, page, size, currentUserId);
        return ResultVO.success("获取用户话题列表成功", topicPage);
    }
    
    /**
     * 设置话题置顶状态
     * @param topicId 话题ID
     * @param isSticky 是否置顶
     * @return 是否成功
     */
    @PutMapping("/{topicId}/sticky")
    @PreAuthorize("hasRole('SYSADMIN')")
    @Operation(summary = "设置话题置顶状态", description = "设置话题是否置顶，需要管理员权限")
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
        @ApiResponse(responseCode = "404", description = "话题不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> setTopicSticky(
            @PathVariable @Parameter(description = "话题ID") Long topicId,
            @RequestParam @Parameter(description = "是否置顶") Boolean isSticky) {
        boolean success = topicService.setTopicSticky(topicId, isSticky);
        if (!success) {
            return ResultVO.error(404, "话题不存在");
        }
        
        return ResultVO.success(isSticky ? "置顶话题成功" : "取消置顶成功");
    }
    
    /**
     * 设置话题精华状态
     * @param topicId 话题ID
     * @param isEssence 是否精华
     * @return 是否成功
     */
    @PutMapping("/{topicId}/essence")
    @PreAuthorize("hasRole('SYSADMIN')")
    @Operation(summary = "设置话题精华状态", description = "设置话题是否为精华，需要管理员权限")
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
        @ApiResponse(responseCode = "404", description = "话题不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> setTopicEssence(
            @PathVariable @Parameter(description = "话题ID") Long topicId,
            @RequestParam @Parameter(description = "是否精华") Boolean isEssence) {
        boolean success = topicService.setTopicEssence(topicId, isEssence);
        if (!success) {
            return ResultVO.error(404, "话题不存在");
        }
        
        return ResultVO.success(isEssence ? "设置精华成功" : "取消精华成功");
    }
    
    /**
     * 获取推荐话题列表
     * @param limit 限制数量
     * @param userDetails 当前登录用户
     * @return 推荐话题列表
     */
    @GetMapping("/recommend")
    @Operation(summary = "获取推荐话题列表", description = "获取推荐话题列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<List<TopicVO>> getRecommendTopics(
            @RequestParam(defaultValue = "5") @Parameter(description = "限制数量") Integer limit,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        
        List<TopicVO> topicList = topicService.getRecommendTopics(userId, limit);
        return ResultVO.success("获取推荐话题列表成功", topicList);
    }
}
