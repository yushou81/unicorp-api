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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "[Admin] 手动创建学校信息", 
            description = "由系统管理员调用，用于录入一个已合作的学校。创建后，学校和默认的学校管理员账号状态均为'approved'/'active'")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "学校及管理员账号创建成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrganizationVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足(非系统管理员)")
    })
    @PostMapping("/organizations/schools")
    public ResponseEntity<ResultVO<OrganizationVO>> createSchool(@Valid @RequestBody SchoolCreationDTO schoolCreationDTO) {
        OrganizationVO organization = organizationService.createSchool(schoolCreationDTO);
        return new ResponseEntity<>(ResultVO.success("学校创建成功", organization), HttpStatus.CREATED);
    }
    
    /**
     * 获取待审核的组织列表
     */
    @Operation(summary = "[Admin] 获取待审核的组织列表", 
            description = "获取所有状态为 'pending' 的组织列表，供管理员审核")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取列表", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrganizationVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足(非系统管理员)")
    })
    @GetMapping("/approvals/organizations")
    public ResponseEntity<ResultVO<List<OrganizationVO>>> getPendingOrganizations() {
        List<OrganizationVO> pendingOrganizations = organizationService.getPendingOrganizations();
        return ResponseEntity.ok(ResultVO.success("获取待审核组织列表成功", pendingOrganizations));
    }
    
    /**
     * 批准企业注册
     */
    @Operation(summary = "[Admin] 批准企业注册", 
            description = "批准一个待审核的企业。后端将该企业及其关联的初始管理员账号的状态都更新为 'approved'/'active'")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "企业已批准", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrganizationVO.class))),
        @ApiResponse(responseCode = "404", description = "企业未找到"),
        @ApiResponse(responseCode = "403", description = "权限不足(非系统管理员)")
    })
    @PutMapping("/enterprises/{id}/approve")
    public ResponseEntity<ResultVO<OrganizationVO>> approveEnterprise(@PathVariable Integer id) {
        OrganizationVO approvedEnterprise = enterpriseService.approveEnterprise(id);
        return ResponseEntity.ok(ResultVO.success("企业审核通过", approvedEnterprise));
    }
} 