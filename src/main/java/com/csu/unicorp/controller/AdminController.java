package com.csu.unicorp.controller;

import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.dto.SchoolCreationDTO;
import com.csu.unicorp.service.EnterpriseService;
import com.csu.unicorp.service.OrganizationService;
import com.csu.unicorp.vo.OrganizationVO;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 管理员接口
 */
@Tag(name = "Admin", description = "系统管理员后台接口")
@RestController
@RequestMapping("/v1/admin")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('" + RoleConstants.ROLE_SYSTEM_ADMIN + "')")
@RequiredArgsConstructor
public class AdminController {
    
    private final OrganizationService organizationService;
    private final EnterpriseService enterpriseService;
    
    /**
     * 创建学校
     */
    @Operation(summary = "创建学校", description = "由系统管理员调用，创建一个新的学校组织。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "学校创建成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PostMapping("/schools")
    public ResultVO<OrganizationVO> createSchool(@Valid @RequestBody SchoolCreationDTO schoolCreationDTO) {
        OrganizationVO organization = organizationService.createSchool(schoolCreationDTO);
        return ResultVO.success("学校创建成功", organization);
    }
    
    /**
     * 获取待审核组织列表
     */
    @Operation(summary = "获取待审核组织列表", description = "获取所有状态为'pending'的组织列表，包括学校和企业。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取组织列表", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/approvals/organizations")
    public ResultVO<List<OrganizationVO>> getPendingOrganizations() {
        List<OrganizationVO> pendingOrganizations = organizationService.getPendingOrganizations();
        return ResultVO.success("获取待审核组织列表成功", pendingOrganizations);
    }
    
    /**
     * 审核通过企业
     */
    @Operation(summary = "审核通过企业", description = "将企业状态从'pending'更新为'approved'，同时激活企业管理员账号。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "企业审核通过", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "企业不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PutMapping("/enterprises/{id}/approve")
    public ResultVO<OrganizationVO> approveEnterprise(@PathVariable Integer id) {
        OrganizationVO approvedEnterprise = enterpriseService.approveEnterprise(id);
        return ResultVO.success("企业审核通过", approvedEnterprise);
    }
} 