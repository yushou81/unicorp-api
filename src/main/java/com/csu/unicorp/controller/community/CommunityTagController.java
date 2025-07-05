package com.csu.unicorp.controller.community;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
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
import com.csu.unicorp.service.CommunityTagService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.community.TagVO;

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
 * 社区标签Controller
 */
@RestController
@RequestMapping("/v1/community/tags")
@RequiredArgsConstructor
@Tag(name = "社区标签API", description = "社区标签相关接口")
public class CommunityTagController {
    
    private final CommunityTagService tagService;
    
    /**
     * 创建标签
     * @param name 标签名称
     * @param description 标签描述
     * @return 标签ID
     */
    @PostMapping
    @PreAuthorize("hasRole('SYSADMIN')")
    @Operation(summary = "创建标签", description = "创建新的标签，需要管理员权限")
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
        @ApiResponse(responseCode = "403", description = "无权创建标签",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Long> createTag(
            @RequestParam @Parameter(description = "标签名称") String name,
            @RequestParam(required = false) @Parameter(description = "标签描述") String description) {
        
        // 检查标签名是否已存在
        if (tagService.existsByName(name)) {
            return ResultVO.error(400, "标签名已存在");
        }
        
        Long tagId = tagService.createTag(name, description);
        return ResultVO.success("创建标签成功", tagId);
    }
    
    /**
     * 更新标签
     * @param tagId 标签ID
     * @param name 标签名称
     * @param description 标签描述
     * @return 是否成功
     */
    @PutMapping("/{tagId}")
    @PreAuthorize("hasRole('SYSADMIN')")
    @Operation(summary = "更新标签", description = "更新标签信息，需要管理员权限")
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
        @ApiResponse(responseCode = "403", description = "无权更新标签",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "标签不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> updateTag(
            @PathVariable @Parameter(description = "标签ID") Long tagId,
            @RequestParam @Parameter(description = "标签名称") String name,
            @RequestParam(required = false) @Parameter(description = "标签描述") String description) {
        
        // 检查标签是否存在
        if (!tagService.existsById(tagId)) {
            return ResultVO.error(404, "标签不存在");
        }
        
        // 检查标签名是否已被其他标签使用
        if (tagService.existsByNameExcludeId(name, tagId)) {
            return ResultVO.error(400, "标签名已被其他标签使用");
        }
        
        boolean success = tagService.updateTag(tagId, name, description);
        return ResultVO.success("更新标签成功");
    }
    
    /**
     * 删除标签
     * @param tagId 标签ID
     * @return 是否成功
     */
    @DeleteMapping("/{tagId}")
    @PreAuthorize("hasRole('SYSADMIN')")
    @Operation(summary = "删除标签", description = "删除标签，需要管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "401", description = "未授权",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "无权删除标签",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "标签不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> deleteTag(
            @PathVariable @Parameter(description = "标签ID") Long tagId) {
        
        // 检查标签是否存在
        if (!tagService.existsById(tagId)) {
            return ResultVO.error(404, "标签不存在");
        }
        
        boolean success = tagService.deleteTag(tagId);
        return ResultVO.success("删除标签成功");
    }
    
    /**
     * 获取标签列表
     * @param page 页码
     * @param size 每页大小
     * @return 标签列表
     */
    @GetMapping
    @Operation(summary = "获取标签列表", description = "分页获取所有标签")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<TagVO>> getTags(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {
        
        Page<TagVO> tagPage = tagService.getTags(page, size);
        return ResultVO.success("获取标签列表成功", tagPage);
    }
    
    /**
     * 获取热门标签列表
     * @param limit 限制数量
     * @return 热门标签列表
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门标签列表", description = "获取使用最多的标签列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<List<TagVO>> getHotTags(
            @RequestParam(defaultValue = "10") @Parameter(description = "限制数量") Integer limit) {
        
        List<TagVO> tagList = tagService.getHotTags(limit);
        return ResultVO.success("获取热门标签列表成功", tagList);
    }
    
    /**
     * 搜索标签
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 标签列表
     */
    @GetMapping("/search")
    @Operation(summary = "搜索标签", description = "根据关键词搜索标签")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "搜索成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<List<TagVO>> searchTags(
            @RequestParam @Parameter(description = "搜索关键词") String keyword,
            @RequestParam(defaultValue = "10") @Parameter(description = "限制数量") Integer limit) {
        
        List<TagVO> tagList = tagService.searchTags(keyword, limit);
        return ResultVO.success("搜索标签成功", tagList);
    }
    
    /**
     * 获取标签详情
     * @param tagId 标签ID
     * @return 标签详情
     */
    @GetMapping("/{tagId}")
    @Operation(summary = "获取标签详情", description = "根据标签ID获取标签详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "标签不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<TagVO> getTagDetail(
            @PathVariable @Parameter(description = "标签ID") Long tagId) {
        
        TagVO tagVO = tagService.getTagDetail(tagId);
        if (tagVO == null) {
            return ResultVO.error(404, "标签不存在");
        }
        
        return ResultVO.success("获取标签详情成功", tagVO);
    }
    
    /**
     * 获取话题的标签列表
     * @param topicId 话题ID
     * @return 标签列表
     */
    @GetMapping("/topic/{topicId}")
    @Operation(summary = "获取话题的标签列表", description = "获取指定话题的标签列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "话题不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<List<TagVO>> getTopicTags(
            @PathVariable @Parameter(description = "话题ID") Long topicId) {
        
        List<TagVO> tagList = tagService.getTopicTags(topicId);
        if (tagList == null) {
            return ResultVO.error(404, "话题不存在");
        }
        
        return ResultVO.success("获取话题标签列表成功", tagList);
    }
    
    /**
     * 获取问题的标签列表
     * @param questionId 问题ID
     * @return 标签列表
     */
    @GetMapping("/question/{questionId}")
    @Operation(summary = "获取问题的标签列表", description = "获取指定问题的标签列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "问题不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<List<TagVO>> getQuestionTags(
            @PathVariable @Parameter(description = "问题ID") Long questionId) {
        
        List<TagVO> tagList = tagService.getQuestionTags(questionId);
        if (tagList == null) {
            return ResultVO.error(404, "问题不存在");
        }
        
        return ResultVO.success("获取问题标签列表成功", tagList);
    }
} 