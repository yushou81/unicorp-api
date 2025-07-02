package com.csu.unicorp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.ApplicationStatusUpdateDTO;
import com.csu.unicorp.service.ApplicationService;
import com.csu.unicorp.vo.ApplicationDetailVO;
import com.csu.unicorp.vo.MyApplicationDetailVO;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 岗位申请管理控制器
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Applications", description = "岗位申请管理")
public class ApplicationController {
    
    private final ApplicationService applicationService;
    
    @PostMapping("/v1/jobs/{id}/apply")
    @PreAuthorize("hasRole('STUDENT')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "[学生] 申请岗位", description = "学生用户申请一个岗位",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "申请成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "已申请过该岗位",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足 (非学生用户)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<Integer> applyJob(
            @Parameter(description = "岗位ID") @PathVariable Integer id,
            @Parameter(description = "简历ID") @RequestParam Integer resumeId,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (resumeId == null) {
            throw new BusinessException("简历ID不能为空");
        }
        // 从UserDetails中获取学生ID
        Integer studentId = getStudentIdFromUserDetails(userDetails);
        
        Integer applicationId = applicationService.applyJob(id, studentId, resumeId);
        return ResultVO.success("申请成功", applicationId);
    }
    
    @GetMapping("/v1/jobs/{id}/applications")
    @PreAuthorize("hasAnyRole('EN_ADMIN', 'EN_TEACHER')")
    @Operation(summary = "[企业] 查看岗位申请列表", description = "企业用户查看指定岗位的申请人列表",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取申请列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<IPage<ApplicationDetailVO>> getApplicationsByJobId(
            @Parameter(description = "岗位ID") @PathVariable Integer id,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 从UserDetails中获取组织ID
        Integer orgId = getOrgIdFromUserDetails(userDetails);
        
        IPage<ApplicationDetailVO> applications = applicationService.pageApplicationsByJobId(id, page, size, orgId);
        return ResultVO.success("获取申请列表成功", applications);
    }
    
    @PatchMapping("/v1/applications/{id}")
    @PreAuthorize("hasAnyRole('EN_ADMIN', 'EN_TEACHER')")
    @Operation(summary = "[企业] 更新申请状态", description = "由企业用户调用，用于更新某个岗位申请的状态（如：标记为面试中、已录用、已拒绝等）",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "状态更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "400", description = "无效的状态值",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "申请未找到",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<ApplicationDetailVO> updateApplicationStatus(
            @Parameter(description = "申请ID") @PathVariable Integer id,
            @Valid @RequestBody ApplicationStatusUpdateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 从UserDetails中获取用户ID和组织ID
        Integer userId = getUserIdFromUserDetails(userDetails);
        Integer orgId = getOrgIdFromUserDetails(userDetails);
        
        ApplicationDetailVO application = applicationService.updateApplicationStatus(id, dto, userId, orgId);
        return ResultVO.success("状态更新成功", application);
    }
    
    @GetMapping("/v1/me/applications")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "[学生] 查看我的申请", description = "获取当前登录学生的所有岗位申请记录及其最新状态",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取申请列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "403", description = "权限不足 (非学生用户)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    public ResultVO<IPage<MyApplicationDetailVO>> getMyApplications(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 从UserDetails中获取学生ID
        Integer studentId = getStudentIdFromUserDetails(userDetails);
        
        IPage<MyApplicationDetailVO> applications = applicationService.pageStudentApplications(studentId, page, size);
        return ResultVO.success("获取我的申请列表成功", applications);
    }
    
    /**
     * 从UserDetails中获取用户ID
     */
    private Integer getUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getUserId();
        }
        throw new IllegalArgumentException("无法从UserDetails中获取用户ID");
    }
    
    /**
     * 从UserDetails中获取组织ID
     */
    private Integer getOrgIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getOrganizationId();
        }
        throw new IllegalArgumentException("无法从UserDetails中获取组织ID");
    }
    
    /**
     * 从UserDetails中获取学生ID
     * 注：在本系统中，学生ID与用户ID相同，因为学生信息是通过userId关联的
     */
    private Integer getStudentIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getUserId();
        }
        throw new IllegalArgumentException("无法从UserDetails中获取学生ID");
    }
} 