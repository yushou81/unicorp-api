package com.csu.unicorp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.service.ApplicationService;
import com.csu.unicorp.vo.ApplicationDetailVO;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 岗位申请控制器
 */
@Tag(name = "Applications", description = "岗位申请管理")
@RestController
@RequiredArgsConstructor
public class ApplicationController {
    
    private final ApplicationService applicationService;
    
    /**
     * 申请岗位
     */
    @Operation(summary = "[学生] 申请岗位", description = "学生用户申请一个岗位")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "申请成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "已申请过该岗位"),
        @ApiResponse(responseCode = "403", description = "权限不足 (非学生用户)")
    })
    @PostMapping("/v1/jobs/{id}/apply")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('" + RoleConstants.ROLE_STUDENT + "')")
    public ResponseEntity<ResultVO<Integer>> applyJob(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Integer applicationId = applicationService.applyJob(id, userDetails);
        return new ResponseEntity<>(ResultVO.success("岗位申请成功", applicationId), HttpStatus.CREATED);
    }
    
    /**
     * 查看岗位申请列表
     */
    @Operation(summary = "[企业] 查看岗位申请列表", description = "企业用户查看指定岗位的申请人列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取申请列表", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ApplicationDetailVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @GetMapping("/v1/jobs/{id}/applications")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('" + RoleConstants.ROLE_ENTERPRISE_ADMIN + "', '" + RoleConstants.ROLE_ENTERPRISE_MENTOR + "')")
    public ResponseEntity<ResultVO<IPage<ApplicationDetailVO>>> getApplications(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        IPage<ApplicationDetailVO> applications = applicationService.getApplicationsByJobId(id, page, size, userDetails);
        return ResponseEntity.ok(ResultVO.success("获取岗位申请列表成功", applications));
    }
} 