package com.csu.unicorp.controller;

import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.dto.OrgMemberCreationDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
} 