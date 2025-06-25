package com.csu.linkneiapi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.linkneiapi.dto.JobApplicationDTO;
import com.csu.linkneiapi.entity.JobApplication;
import com.csu.linkneiapi.service.JobApplicationService;
import com.csu.linkneiapi.vo.JobApplicationVO;
import com.csu.linkneiapi.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位投递相关API
 */
@RestController
@RequestMapping("/job-applications")
@RequiredArgsConstructor
@Tag(name = "投递记录管理", description = "岗位投递相关操作接口")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;
    
    @Operation(summary = "投递岗位", description = "用户投递简历到指定岗位")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "投递成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误或已投递过该岗位",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PostMapping
    public ResultVO<Void> applyJob(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody JobApplicationDTO applicationDTO) {
        // 从userDetails中获取用户ID (这里假设UserDetails实现中包含了用户ID)
        Long userId = Long.valueOf(userDetails.getUsername());
        
        boolean success = jobApplicationService.applyJob(userId, applicationDTO);
        if (success) {
            return ResultVO.success();
        } else {
            return ResultVO.error(500, "投递失败，请稍后重试");
        }
    }
    
    @Operation(summary = "查询我的投递记录", description = "查询当前登录用户的所有投递记录")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/my")
    public ResultVO<List<JobApplicationVO>> getMyApplications(@AuthenticationPrincipal UserDetails userDetails) {
        // 从userDetails中获取用户ID
        Long userId = Long.valueOf(userDetails.getUsername());
        
        List<JobApplicationVO> applications = jobApplicationService.getUserApplications(userId);
        return ResultVO.success(applications);
    }
    
    @Operation(summary = "查询企业收到的投递", description = "查询指定企业收到的所有投递记录（需企业成员权限）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "没有权限",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/enterprise/{enterpriseId}")
    @PreAuthorize("@securityService.isEnterpriseMember(#enterpriseId, authentication)")
    public ResultVO<?> getEnterpriseApplications(
            @PathVariable Long enterpriseId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Page<JobApplication> pageParam = new Page<>(page, size);
        return ResultVO.success(jobApplicationService.pageEnterpriseApplications(enterpriseId, status, pageParam));
    }
    
    @Operation(summary = "更新投递状态", description = "企业HR更新应聘者的投递状态（需企业成员权限）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "没有权限",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PutMapping("/{applicationId}/status")
    public ResultVO<Void> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Parameter(description = "新状态: SUBMITTED, VIEWED, INTERVIEWING, OFFERED, REJECTED") 
            @RequestParam String status,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long operatorId = Long.valueOf(userDetails.getUsername());
        boolean success = jobApplicationService.updateApplicationStatus(applicationId, status, operatorId);
        
        if (success) {
            return ResultVO.success();
        } else {
            return ResultVO.error(500, "更新状态失败，请稍后重试");
        }
    }
    
    @Operation(summary = "获取投递详情", description = "获取单个投递记录的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "查询成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "记录不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/{applicationId}")
    public ResultVO<JobApplicationVO> getApplicationDetail(@PathVariable Long applicationId) {
        return ResultVO.success(jobApplicationService.getApplicationDetail(applicationId));
    }
} 