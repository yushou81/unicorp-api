package com.csu.unicorp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.MentorCreationDTO;
import com.csu.unicorp.dto.MentorUpdateDTO;
import com.csu.unicorp.dto.OrgMemberCreationDTO;
import com.csu.unicorp.dto.OrgMemberUpdateDTO;
import com.csu.unicorp.service.EnterpriseAdminService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.UserVO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 企业管理员控制器
 */
@Tag(name = "Enterprise Admin", description = "企业管理员专属接口")
@RestController
@RequestMapping("/v1/enterprise-admin")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('" + RoleConstants.ROLE_ENTERPRISE_ADMIN + "')")
@RequiredArgsConstructor
public class EnterpriseAdminController {

    private final UserService userService;
    private final EnterpriseAdminService enterpriseAdminService;

    /**
     * 查询本企业所有用户
     */
    @Operation(summary = "查询本企业所有用户", description = "获取本企业的所有用户列表（包括企业导师等），支持分页。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户列表",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/users")
    public ResultVO<Page<UserVO>> getAllUsers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        Page<UserVO> pageResult = enterpriseAdminService.getAllUsers(
                userDetails.getOrganizationId(), page, size);
        
        return ResultVO.success("获取用户列表成功", pageResult);
    }

    /**
     * 创建企业导师账号
     */
    @Operation(summary = "创建企业导师账号", description = "由企业管理员调用，为自己的企业创建新的导师账号。后端自动生成账号，状态为'active'。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "导师账号创建成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PostMapping("/mentors")
    public ResultVO<UserVO> createMentor(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MentorCreationDTO mentorCreationDTO) {
        
        UserVO mentor = enterpriseAdminService.createMentor(
                userDetails.getOrganizationId(), mentorCreationDTO);
        
        return ResultVO.success("导师账号创建成功", mentor);
    }
    
    /**
     * 获取导师列表
     */
    @Operation(summary = "[企业管理员] 查询本企业导师列表", 
            description = "由企业管理员调用，获取本企业的所有导师账号列表，支持分页。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取导师列表", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足(非企业管理员)")
    })
    @GetMapping("/mentors")
    public ResultVO<IPage<UserVO>> getMentors(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        IPage<UserVO> mentors = userService.getMentors(page, size, userDetails);
        return ResultVO.success("获取导师列表成功", mentors);
    }
    
    /**
     * 更新导师信息
     */
    @Operation(summary = "更新导师信息", description = "更新本企业指定导师的非敏感信息（如昵称、电话）。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "用户未找到",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PutMapping("/mentors/{id}")
    public ResultVO<UserVO> updateMentor(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id,
            @RequestBody MentorUpdateDTO mentorUpdateDTO) {
        
        UserVO mentor = enterpriseAdminService.updateMentor(
                userDetails.getOrganizationId(), id, mentorUpdateDTO);
        
        return ResultVO.success("导师信息更新成功", mentor);
    }
    
    /**
     * 禁用导师账号
     */
    @Operation(summary = "禁用导师账号", description = "禁用本企业的某个导师账号 (将其状态更新为 'inactive')。这是一个可逆操作。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "禁用成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @DeleteMapping("/mentors/{id}")
    public ResultVO<Void> disableMentor(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id) {
        
        enterpriseAdminService.disableMentor(userDetails.getOrganizationId(), id);
        
        return ResultVO.success("导师账号禁用成功");
    }
} 