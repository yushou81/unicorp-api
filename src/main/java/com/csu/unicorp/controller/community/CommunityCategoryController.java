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
import com.csu.unicorp.dto.community.CategoryDTO;
import com.csu.unicorp.service.CommunityCategoryService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.community.CategoryVO;
import com.csu.unicorp.vo.community.DeleteResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

/**
 * 社区板块Controller
 */
@RestController
@RequestMapping("/v1/community/categories")
@RequiredArgsConstructor
@Tag(name = "社区板块API", description = "社区板块相关接口")
@Slf4j
public class CommunityCategoryController {
    
    private final CommunityCategoryService categoryService;
    
    /**
     * 获取板块树形结构
     * @return 板块树形结构
     */
    @GetMapping("/tree")
    @Operation(summary = "获取板块树形结构", description = "获取板块的树形结构，包含父子关系")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<List<CategoryVO>> getCategoryTree() {
        log.info("获取板块树形结构");
        List<CategoryVO> categoryTree = categoryService.getCategoryTree();
        return ResultVO.success("获取板块树形结构成功", categoryTree);
    }
    
    /**
     * 获取板块详情
     * @param categoryId 板块ID
     * @return 板块详情
     */
    @GetMapping("/{categoryId}")
    @Operation(summary = "获取板块详情", description = "根据板块ID获取板块详情")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "板块不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<CategoryVO> getCategoryDetail(
            @PathVariable @Parameter(description = "板块ID") Long categoryId) {
        CategoryVO categoryVO = categoryService.getCategoryDetail(categoryId);
        if (categoryVO == null) {
            return ResultVO.error(404, "板块不存在");
        }
        return ResultVO.success("获取板块详情成功", categoryVO);
    }
    
    /**
     * 获取板块列表（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 板块列表
     */
    @GetMapping
    @Operation(summary = "获取板块列表", description = "分页获取板块列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Page<CategoryVO>> listCategories(
            @RequestParam(defaultValue = "1") @Parameter(description = "页码") Integer page,
            @RequestParam(defaultValue = "10") @Parameter(description = "每页大小") Integer size) {
        Page<CategoryVO> categoryPage = categoryService.listCategories(page, size);
        return ResultVO.success("获取板块列表成功", categoryPage);
    }
    
    /**
     * 创建板块
     * @param categoryDTO 板块DTO
     * @return 板块ID
     */
    @PostMapping
    @PreAuthorize("hasRole('SYSADMIN')")
    @Operation(summary = "创建板块", description = "创建新的板块，需要管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "创建成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Long> createCategory(
            @RequestBody @Valid @Parameter(description = "板块信息") CategoryDTO categoryDTO) {
        Long categoryId = categoryService.createCategory(categoryDTO);
        return ResultVO.success("创建板块成功", categoryId);
    }
    
    /**
     * 更新板块
     * @param categoryId 板块ID
     * @param categoryDTO 板块DTO
     * @return 是否成功
     */
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('SYSADMIN')")
    @Operation(summary = "更新板块", description = "更新板块信息，需要管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "板块不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> updateCategory(
            @PathVariable @Parameter(description = "板块ID") Long categoryId,
            @RequestBody @Valid @Parameter(description = "板块信息") CategoryDTO categoryDTO) {
        boolean success = categoryService.updateCategory(categoryId, categoryDTO);
        if (!success) {
            return ResultVO.error(404, "板块不存在");
        }
        return ResultVO.success("更新板块成功");
    }
    
    /**
     * 删除板块
     * @param categoryId 板块ID
     * @return 是否成功
     */
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('SYSADMIN')")
    @Operation(summary = "删除板块", description = "删除板块，需要管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "板块不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "板块存在子板块或话题，无法删除",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> deleteCategory(
            @PathVariable @Parameter(description = "板块ID") Long categoryId) {
        DeleteResult result = categoryService.deleteCategory(categoryId);
        if (!result.isSuccess()) {
            if ("板块不存在".equals(result.getErrorMessage())) {
                return ResultVO.error(404, result.getErrorMessage());
            } else {
                return ResultVO.error(400, result.getErrorMessage());
            }
        }
        return ResultVO.success("删除板块成功");
    }
    
    /**
     * 更新板块排序
     * @param categoryId 板块ID
     * @param sortOrder 排序顺序
     * @return 是否成功
     */
    @PutMapping("/{categoryId}/sort")
    @PreAuthorize("hasRole('SYSADMIN')")
    @Operation(summary = "更新板块排序", description = "更新板块排序顺序，需要管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "板块不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> updateCategorySortOrder(
            @PathVariable @Parameter(description = "板块ID") Long categoryId,
            @RequestParam @Parameter(description = "排序顺序") Integer sortOrder) {
        boolean success = categoryService.updateCategorySortOrder(categoryId, sortOrder);
        if (!success) {
            return ResultVO.error(404, "板块不存在");
        }
        return ResultVO.success("更新板块排序成功");
    }
    
    /**
     * 获取用户可见的板块列表
     * @param userDetails 当前登录用户
     * @return 板块列表
     */
    @GetMapping("/visible")
    @Operation(summary = "获取用户可见的板块列表", description = "获取当前登录用户可见的板块列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<List<CategoryVO>> getUserVisibleCategories(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails != null ? Long.valueOf(userDetails.getUser().getId()) : null;
        List<CategoryVO> categories = categoryService.getUserVisibleCategories(userId);
        return ResultVO.success("获取用户可见板块列表成功", categories);
    }
} 