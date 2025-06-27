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

    /**
     * 创建企业导师账号
     */
    @Operation(summary = "[企业管理员] 创建企业导师账号", 
            description = "由企业管理员调用，为自己的企业创建新的导师账号。后端自动生成账号，状态为'active'。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "导师账号创建成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = UserVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足(非企业管理员)"),
        @ApiResponse(responseCode = "400", description = "邮箱已存在")
    })
    @PostMapping("/mentors")
    public ResponseEntity<ResultVO<UserVO>> createMentor(
            @Valid @RequestBody OrgMemberCreationDTO mentorDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserVO mentor = userService.createMentor(mentorDTO, userDetails);
        return new ResponseEntity<>(ResultVO.success("企业导师账号创建成功", mentor), HttpStatus.CREATED);
    }
    
    /**
     * 获取导师列表
     */
    @Operation(summary = "[企业管理员] 查询本企业导师列表", 
            description = "由企业管理员调用，获取本企业的所有导师账号列表，支持分页。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取导师列表", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = UserVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足(非企业管理员)")
    })
    @GetMapping("/mentors")
    public ResponseEntity<ResultVO<IPage<UserVO>>> getMentors(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        IPage<UserVO> mentors = userService.getMentors(page, size, userDetails);
        return ResponseEntity.ok(ResultVO.success("获取导师列表成功", mentors));
    }
    
    /**
     * 更新导师信息
     */
    @Operation(summary = "[企业管理员] 更新导师信息", 
            description = "更新本企业指定导师的非敏感信息（如昵称、电话）。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = UserVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足(非企业管理员)"),
        @ApiResponse(responseCode = "404", description = "用户未找到")
    })
    @PutMapping("/mentors/{id}")
    public ResponseEntity<ResultVO<UserVO>> updateMentor(
            @PathVariable Integer id,
            @Valid @RequestBody OrgMemberUpdateDTO updateDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserVO mentor = userService.updateMentor(id, updateDTO, userDetails);
        return ResponseEntity.ok(ResultVO.success("导师信息更新成功", mentor));
    }
    
    /**
     * 禁用导师账号
     */
    @Operation(summary = "[企业管理员] 禁用导师账号", 
            description = "禁用本企业的某个导师账号 (将其状态更新为 'inactive')。这是一个可逆操作。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "禁用成功"),
        @ApiResponse(responseCode = "403", description = "权限不足(非企业管理员)"),
        @ApiResponse(responseCode = "404", description = "用户未找到")
    })
    @DeleteMapping("/mentors/{id}")
    public ResponseEntity<Void> disableMentor(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.disableMentor(id, userDetails);
        return ResponseEntity.noContent().build();
    }
} 