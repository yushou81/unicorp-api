package com.csu.unicorp.controller;

import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.dto.job.JobCategoryCreationDTO;
import com.csu.unicorp.dto.job.JobCategoryUpdateDTO;
import com.csu.unicorp.service.JobCategoryService;
import com.csu.unicorp.vo.JobCategoryVO;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位分类管理控制器
 */
@Tag(name = "Job Categories", description = "岗位分类管理")
@RestController
@RequiredArgsConstructor
public class JobCategoryController {

    private final JobCategoryService jobCategoryService;

    /**
     * 获取所有顶级分类
     */
    @Operation(summary = "获取所有顶级分类", description = "获取所有一级分类，包含其子分类")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/v1/job-categories/root")
    public ResultVO<List<JobCategoryVO>> getRootCategories() {
        List<JobCategoryVO> categories = jobCategoryService.getRootCategories();
        return ResultVO.success("获取顶级分类成功", categories);
    }

    /**
     * 获取指定分类的子分类
     */
    @Operation(summary = "获取指定分类的子分类", description = "获取指定分类ID下的所有直接子分类")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @GetMapping("/v1/job-categories/{id}/children")
    public ResultVO<List<JobCategoryVO>> getChildCategories(
            @Parameter(description = "分类ID") @PathVariable Integer id) {
        List<JobCategoryVO> categories = jobCategoryService.getChildCategories(id);
        return ResultVO.success("获取子分类成功", categories);
    }

    /**
     * 获取所有分类（平铺结构）
     */
    @Operation(summary = "获取所有分类", description = "获取所有分类，以平铺结构返回")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/v1/job-categories")
    public ResultVO<List<JobCategoryVO>> getAllCategories() {
        List<JobCategoryVO> categories = jobCategoryService.getAllCategories();
        return ResultVO.success("获取所有分类成功", categories);
    }

    /**
     * 获取分类详情
     */
    @Operation(summary = "获取分类详情", description = "获取指定ID的分类详情")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @GetMapping("/v1/job-categories/{id}")
    public ResultVO<JobCategoryVO> getCategoryById(
            @Parameter(description = "分类ID") @PathVariable Integer id) {
        JobCategoryVO category = jobCategoryService.getCategoryById(id);
        return ResultVO.success("获取分类详情成功", category);
    }

    /**
     * 创建分类（仅系统管理员）
     */
    @Operation(summary = "[系统管理员] 创建分类", description = "创建新的岗位分类")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping("/v1/admin/job-categories")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_SYSTEM_ADMIN + "')")
    public ResultVO<JobCategoryVO> createCategory(
            @Valid @RequestBody JobCategoryCreationDTO dto) {
        JobCategoryVO category = jobCategoryService.createCategory(dto);
        return ResultVO.success("创建分类成功", category);
    }

    /**
     * 更新分类（仅系统管理员）
     */
    @Operation(summary = "[系统管理员] 更新分类", description = "更新指定ID的岗位分类")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @PutMapping("/v1/admin/job-categories/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_SYSTEM_ADMIN + "')")
    public ResultVO<JobCategoryVO> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Integer id,
            @Valid @RequestBody JobCategoryUpdateDTO dto) {
        JobCategoryVO category = jobCategoryService.updateCategory(id, dto);
        return ResultVO.success("更新分类成功", category);
    }

    /**
     * 删除分类（仅系统管理员）
     */
    @Operation(summary = "[系统管理员] 删除分类", description = "删除指定ID的岗位分类，如果有子分类则无法删除")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "存在子分类，无法删除"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "分类不存在")
    })
    @DeleteMapping("/v1/admin/job-categories/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_SYSTEM_ADMIN + "')")
    public ResultVO<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Integer id) {
        jobCategoryService.deleteCategory(id);
        return ResultVO.success("删除分类成功");
    }
} 