package com.csu.unicorp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.dto.OrgMemberCreationDTO;
import com.csu.unicorp.dto.OrgMemberUpdateDTO;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.ResultVO;
import com.csu.unicorp.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
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

/**
 * 学校管理员控制器
 */
@Tag(name = "School Admin", description = "学校管理员专属接口")
@RestController
@RequestMapping("/v1/school-admin")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('" + RoleConstants.ROLE_SCHOOL_ADMIN + "')")
@RequiredArgsConstructor
public class SchoolAdminController {

    private final UserService userService;

    /**
     * 创建教师账号
     */
    @Operation(summary = "[学校管理员] 创建教师账号", 
            description = "由学校管理员调用，为自己的学校创建新的教师账号。后端自动生成账号，状态为'active'。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "教师账号创建成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = UserVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足(非学校管理员)"),
        @ApiResponse(responseCode = "400", description = "邮箱已存在")
    })
    @PostMapping("/teachers")
    public ResponseEntity<ResultVO<UserVO>> createTeacher(
            @Valid @RequestBody OrgMemberCreationDTO teacherDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserVO teacher = userService.createTeacher(teacherDTO, userDetails);
        return new ResponseEntity<>(ResultVO.success("教师账号创建成功", teacher), HttpStatus.CREATED);
    }
    
    /**
     * 获取教师列表
     */
    @Operation(summary = "[学校管理员] 查询本校教师列表", 
            description = "由学校管理员调用，获取本校的所有教师账号列表，支持分页。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取教师列表", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = UserVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足(非学校管理员)")
    })
    @GetMapping("/teachers")
    public ResponseEntity<ResultVO<IPage<UserVO>>> getTeachers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        IPage<UserVO> teachers = userService.getTeachers(page, size, userDetails);
        return ResponseEntity.ok(ResultVO.success("获取教师列表成功", teachers));
    }
    
    /**
     * 更新教师信息
     */
    @Operation(summary = "[学校管理员] 更新教师信息", 
            description = "更新本校指定教师的非敏感信息（如昵称、电话）。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = UserVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足(非学校管理员)"),
        @ApiResponse(responseCode = "404", description = "用户未找到")
    })
    @PutMapping("/teachers/{id}")
    public ResponseEntity<ResultVO<UserVO>> updateTeacher(
            @PathVariable Integer id,
            @Valid @RequestBody OrgMemberUpdateDTO updateDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserVO teacher = userService.updateTeacher(id, updateDTO, userDetails);
        return ResponseEntity.ok(ResultVO.success("教师信息更新成功", teacher));
    }
    
    /**
     * 禁用教师账号
     */
    @Operation(summary = "[学校管理员] 禁用教师账号", 
            description = "禁用本校的某个教师账号 (将其状态更新为 'inactive')。这是一个可逆操作。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "禁用成功"),
        @ApiResponse(responseCode = "403", description = "权限不足(非学校管理员)"),
        @ApiResponse(responseCode = "404", description = "用户未找到")
    })
    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<Void> disableTeacher(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.disableTeacher(id, userDetails);
        return ResponseEntity.noContent().build();
    }
} 