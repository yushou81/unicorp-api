package com.csu.unicorp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.JobCreationDTO;
import com.csu.unicorp.service.JobService;
import com.csu.unicorp.vo.JobVO;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 岗位管理控制器
 */
@RestController
@RequestMapping("/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "招聘岗位管理")
public class JobController {
    
    private final JobService jobService;
    
    @GetMapping
    @Operation(summary = "获取岗位列表", description = "获取所有状态为 'open' 的岗位列表，支持分页和搜索")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取岗位列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<IPage<JobVO>> getJobs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        IPage<JobVO> jobList = jobService.pageJobs(page, size, keyword);
        return ResultVO.success("获取岗位列表成功", jobList);
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('EN_ADMIN', 'EN_TEACHER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "[企业] 创建新岗位", description = "由企业管理员或企业导师调用，用于发布一个新的招聘岗位",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "岗位创建成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Integer> createJob(
            @Valid @RequestBody JobCreationDTO jobCreationDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 从UserDetails中获取用户ID和组织ID
        Integer userId = getUserIdFromUserDetails(userDetails);
        System.out.println(userId);
        Integer orgId = getOrgIdFromUserDetails(userDetails);
        
        Integer jobId = jobService.createJob(jobCreationDTO, userId, orgId);
        return ResultVO.success("岗位创建成功", jobId);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取特定岗位详情", description = "获取指定ID的岗位详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取岗位详情",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "岗位未找到",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<JobVO> getJobDetail(@Parameter(description = "岗位ID") @PathVariable Integer id) {
        JobVO job = jobService.getJobDetail(id);
        return ResultVO.success("获取岗位详情成功", job);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EN_ADMIN', 'EN_TEACHER')")
    @Operation(summary = "[企业] 更新岗位信息", description = "由企业管理员或企业导师调用，用于更新已发布岗位的信息",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "岗位更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "岗位未找到",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Boolean> updateJob(
            @Parameter(description = "岗位ID") @PathVariable Integer id,
            @Valid @RequestBody JobCreationDTO jobCreationDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 从UserDetails中获取用户ID和组织ID
        Integer userId = getUserIdFromUserDetails(userDetails);
        Integer orgId = getOrgIdFromUserDetails(userDetails);
        
        boolean success = jobService.updateJob(id, jobCreationDTO, userId, orgId);
        return ResultVO.success("岗位更新成功", success);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EN_ADMIN', 'EN_TEACHER')")
    @Operation(summary = "[企业] 删除岗位", description = "由企业管理员或企业导师调用，用于删除已发布的岗位",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Void> deleteJob(
            @Parameter(description = "岗位ID") @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 从UserDetails中获取用户ID和组织ID
        Integer userId = getUserIdFromUserDetails(userDetails);
        Integer orgId = getOrgIdFromUserDetails(userDetails);
        
        jobService.deleteJob(id, userId, orgId);
        return ResultVO.success("岗位删除成功");
    }
    
    /**
     * 从UserDetails中获取用户ID
     */
    private Integer getUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getUserId();
        }
        throw new IllegalArgumentException("UserDetails不是CustomUserDetails类型");
    }
    
    /**
     * 从UserDetails中获取组织ID
     */
    private Integer getOrgIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getOrganizationId();
        }
        throw new IllegalArgumentException("UserDetails不是CustomUserDetails类型");
    }
} 