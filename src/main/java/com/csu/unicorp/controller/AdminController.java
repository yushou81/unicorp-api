package com.csu.unicorp.controller;

import com.csu.unicorp.dto.SchoolCreationDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * 管理员接口
 */
@Tag(name = "Admin", description = "系统管理员后台接口")
@RestController
@RequestMapping("/v1/admin")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('SYSADMIN')")
@RequiredArgsConstructor
public class AdminController {
    
    private final OrganizationService organizationService;
    
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
} 