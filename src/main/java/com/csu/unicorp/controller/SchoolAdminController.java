package com.csu.unicorp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.TeacherCreationDTO;
import com.csu.unicorp.dto.TeacherUpdateDTO;
import com.csu.unicorp.dto.UserUpdateDTO;
import com.csu.unicorp.service.SchoolAdminService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学校管理员控制器
 */
@Tag(name = "School Admin", description = "学校管理员专属接口")
@RestController
@RequestMapping("/v1/school-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SCH_ADMIN')")
public class SchoolAdminController {
    
    private final SchoolAdminService schoolAdminService;
    
    /**
     * 查询本校所有用户
     */
    @Operation(summary = "查询本校所有用户", description = "获取本校的所有用户列表（包括教师和学生），支持分页和按角色筛选。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取用户列表",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/users")
    public ResultVO<Page<UserVO>> getAllUsers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "按角色ID筛选") @RequestParam(required = false) Integer roleId) {
        
        Page<UserVO> pageResult = schoolAdminService.getAllUsers(
                userDetails.getOrganizationId(), page, size, roleId);
        return ResultVO.success("获取用户列表成功", pageResult);
    }
    
    /**
     * 创建教师账号
     */
    @Operation(summary = "创建教师账号", description = "由学校管理员调用，为自己的学校创建新的教师账号。后端自动生成账号，状态为'active'。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "教师账号创建成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/teachers")
    public ResultVO<UserVO> createTeacher(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody TeacherCreationDTO teacherCreationDTO) {
        
        UserVO teacher = schoolAdminService.createTeacher(userDetails.getOrganizationId(), teacherCreationDTO);
        
        return ResultVO.success("教师账号创建成功", teacher);
    }
    
    /**
     * 更新用户基本信息
     */
    @Operation(summary = "更新用户基本信息", description = "更新本校用户(学生或教师)的基本信息，包括邮箱、手机号和昵称")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "邮箱或手机号已被使用",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "用户不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/users/{id}/info")
    public ResultVO<UserVO> updateUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        
        UserVO updatedUser = schoolAdminService.updateUserInfo(userDetails.getOrganizationId(), id, userUpdateDTO);
        
        return ResultVO.success("用户信息更新成功", updatedUser);
    }
    
    /**
     * 更新用户状态
     */
    @Operation(summary = "更新用户状态", description = "更新本校用户(学生或教师)的状态，可选值：active(启用), inactive(禁用), pending_approval(待审核)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "状态更新成功"),
        @ApiResponse(responseCode = "400", description = "无效的状态值或用户已处于该状态",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "用户不存在",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/users/{id}/status")
    public ResultVO<Void> updateUserStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id,
            @RequestParam String status) {
        
        schoolAdminService.updateUserStatus(userDetails.getOrganizationId(), id, status);
        
        return ResultVO.success("用户状态更新成功");
    }
    
    /**
     * 禁用用户账号 (兼容旧版API)
     * @deprecated 请使用 {@link #updateUserStatus(CustomUserDetails, Integer, String)} 替代
     */
    @Deprecated
    @Operation(summary = "禁用用户账号", description = "禁用本校的某个用户账号(学生或教师) (将其状态更新为 'inactive')。这是一个可逆操作。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "禁用成功"),
        @ApiResponse(responseCode = "400", description = "用户已处于禁用状态",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/users/{id}")
    public ResultVO<Void> disableUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id) {
        
        schoolAdminService.updateUserStatus(userDetails.getOrganizationId(), id, "inactive");
        
        return ResultVO.success("用户账号禁用成功");
    }
    
    /**
     * 启用用户账号 (兼容旧版API)
     * @deprecated 请使用 {@link #updateUserStatus(CustomUserDetails, Integer, String)} 替代
     */
    @Deprecated
    @Operation(summary = "启用用户账号", description = "启用本校的某个被禁用的用户账号(学生或教师) (将其状态更新为 'active')。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "启用成功"),
        @ApiResponse(responseCode = "400", description = "用户已处于启用状态",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/users/{id}/enable")
    public ResultVO<Void> enableUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id) {
        
        schoolAdminService.updateUserStatus(userDetails.getOrganizationId(), id, "active");
        
        return ResultVO.success("用户账号启用成功");
    }
    
    /**
     * 禁用教师账号 (兼容旧版API)
     * @deprecated 请使用 {@link #updateUserStatus(CustomUserDetails, Integer, String)} 替代
     */
    @Deprecated
    @Operation(summary = "禁用教师账号", description = "禁用本校的某个教师账号 (将其状态更新为 'inactive')。这是一个可逆操作。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "禁用成功",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/teachers/{id}")
    public ResultVO<Void> disableTeacher(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id) {
        
        schoolAdminService.updateUserStatus(userDetails.getOrganizationId(), id, "inactive");
        
        return ResultVO.success("教师账号禁用成功");
    }
    
    /**
     * 启用教师账号 (兼容旧版API)
     * @deprecated 请使用 {@link #updateUserStatus(CustomUserDetails, Integer, String)} 替代
     */
    @Deprecated
    @Operation(summary = "启用教师账号", description = "启用本校的某个被禁用的教师账号 (将其状态更新为 'active')。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "启用成功"),
        @ApiResponse(responseCode = "400", description = "教师已处于启用状态",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/teachers/{id}/enable")
    public ResultVO<Void> enableTeacher(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id) {
        
        schoolAdminService.updateUserStatus(userDetails.getOrganizationId(), id, "active");
        
        return ResultVO.success("教师账号启用成功");
    }
    
    /**
     * 更新教师信息 (兼容旧版API)
     * @deprecated 请使用 {@link #updateUserInfo(CustomUserDetails, Integer, UserUpdateDTO)} 替代
     */
    @Deprecated
    @Operation(summary = "更新教师信息", description = "更新本校指定教师的非敏感信息（如昵称、电话）。")
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
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/teachers/{id}")
    public ResultVO<UserVO> updateTeacher(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer id,
            @RequestBody TeacherUpdateDTO teacherUpdateDTO) {
        
        // 创建一个UserUpdateDTO并复制TeacherUpdateDTO中的值
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setNickname(teacherUpdateDTO.getNickname());
        userUpdateDTO.setPhone(teacherUpdateDTO.getPhone());
        
        // 调用通用的更新方法
        UserVO teacher = schoolAdminService.updateUserInfo(userDetails.getOrganizationId(), id, userUpdateDTO);
        
        return ResultVO.success("教师信息更新成功", teacher);
    }
} 