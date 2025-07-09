package com.csu.unicorp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.dto.SchoolCreationDTO;
import com.csu.unicorp.dto.UserStatusUpdateDTO;
import com.csu.unicorp.dto.UserUpdateDTO;
import com.csu.unicorp.service.EnterpriseService;
import com.csu.unicorp.service.OrganizationService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.OrganizationVO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 管理员接口
 */
@Tag(name = "SYSAdmin", description = "系统管理员后台接口")
@RestController
@RequestMapping("/v1/admin")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('" + RoleConstants.ROLE_SYSTEM_ADMIN + "')")
@RequiredArgsConstructor
public class AdminController {
    
    private final OrganizationService organizationService;
    private final EnterpriseService enterpriseService;
    private final UserService userService;
    
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
    @PostMapping(value = "/schools", consumes = "multipart/form-data")
    public ResultVO<OrganizationVO> createSchool(
            @RequestParam @NotBlank(message = "学校名称不能为空") String organizationName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String website,
            @RequestParam(required = false) String adminNickname,
            @RequestParam @NotBlank(message = "管理员密码不能为空") String adminPassword,
            @RequestParam @NotBlank(message = "管理员邮箱不能为空") @Email(message = "管理员邮箱格式不正确") String adminEmail,
            @RequestPart(value = "logo", required = false) MultipartFile logo) {
        
        // 组装SchoolCreationDTO对象
        SchoolCreationDTO schoolCreationDTO = new SchoolCreationDTO();
        schoolCreationDTO.setOrganizationName(organizationName);
        schoolCreationDTO.setDescription(description);
        schoolCreationDTO.setAddress(address);
        schoolCreationDTO.setWebsite(website);
        schoolCreationDTO.setAdminNickname(adminNickname);
        schoolCreationDTO.setAdminPassword(adminPassword);
        schoolCreationDTO.setAdminEmail(adminEmail);
        
        // 处理logo上传
        if (logo != null && !logo.isEmpty()) {
            schoolCreationDTO.setLogoFile(logo);
        }
        
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
    
    /**
     * 获取用户列表（可根据角色筛选）
     */
    @Operation(summary = "获取用户列表", description = "获取所有用户列表，不包括系统管理员，可根据角色进行筛选。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户列表", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/users")
    public ResultVO<IPage<UserVO>> getUsers(
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "角色名称，可选值：STUDENT, TEACHER, SCH_ADMIN, ENT_ADMIN, EN_TEACHER") 
            @RequestParam(required = false) String role) {
        IPage<UserVO> users = userService.getUsersByRole(page, size, role);
        return ResultVO.success("获取用户列表成功", users);
    }
    
    /**
     * 修改用户状态
     */
    @Operation(summary = "修改用户状态", description = "修改指定用户的状态，如激活或禁用用户。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "用户状态修改成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "用户不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PutMapping("/users/{id}/status")
    public ResultVO<UserVO> updateUserStatus(
            @PathVariable Integer id, 
            @Valid @RequestBody UserStatusUpdateDTO statusUpdateDTO) {
        UserVO updatedUser = userService.updateUserStatus(id, statusUpdateDTO.getStatus());
        return ResultVO.success("用户状态修改成功", updatedUser);
    }
    
    /**
     * 修改用户基本信息
     */
    @Operation(summary = "修改用户基本信息", description = "修改指定用户的昵称、邮箱、手机号等基本信息。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "用户信息修改成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "用户不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "邮箱或手机号已被使用", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PutMapping("/users/{id}")
    public ResultVO<UserVO> updateUser(
            @PathVariable Integer id, 
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserVO updatedUser = userService.updateUserByAdmin(id, userUpdateDTO);
        return ResultVO.success("用户信息修改成功", updatedUser);
    }
} 