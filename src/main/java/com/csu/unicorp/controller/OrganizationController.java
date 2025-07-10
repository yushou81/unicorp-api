package com.csu.unicorp.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.csu.unicorp.service.OrganizationService;
import com.csu.unicorp.vo.OrganizationVO;
import com.csu.unicorp.vo.ResultVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 组织相关接口
 */
@Tag(name = "Organizations", description = "组织管理")
@RestController
@RequestMapping("/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {
    
    private final OrganizationService organizationService;
    
    /**
     * 获取所有学校列表
     */
    @Operation(summary = "获取学校列表(公开)", description = "获取所有已批准的学校列表。可通过`view`参数控制返回数据的详细程度。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取学校列表", 
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/schools")
    public ResultVO<List<?>> getAllSchools(
            @Parameter(description = "视图类型，决定返回数据的详细程度", schema = @Schema(type = "string", allowableValues = {"simple", "detailed"}, defaultValue = "simple"))
            @RequestParam(required = false, defaultValue = "simple") String view) {
        
        List<?> schools = organizationService.getAllSchools(view);
        return ResultVO.success("获取学校列表成功", schools);
    }
    
    /**
     * 获取单个学校详情
     */
    @Operation(summary = "获取学校详情(公开)", description = "根据ID获取单个学校的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取学校详情", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "学校不存在")
    })
    @GetMapping("/schools/{id}")
    public ResultVO<OrganizationVO> getSchoolById(@PathVariable Integer id) {
        OrganizationVO school = organizationService.getSchoolById(id);
        return ResultVO.success("获取学校详情成功", school);
    }
    
    /**
     * 获取所有企业列表
     */
    @Operation(summary = "获取企业列表(公开)", description = "获取所有已批准的企业列表。可通过`view`参数控制返回数据的详细程度。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取企业列表", 
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/enterprises")
    public ResultVO<List<?>> getAllEnterprises(
            @Parameter(description = "视图类型，决定返回数据的详细程度", schema = @Schema(type = "string", allowableValues = {"simple", "detailed"}, defaultValue = "simple"))
            @RequestParam(required = false, defaultValue = "simple") String view) {
        
        List<?> enterprises = organizationService.getAllEnterprises(view);
        return ResultVO.success("获取企业列表成功", enterprises);
    }
    
    /**
     * 获取单个企业详情
     */
    @Operation(summary = "获取企业详情(公开)", description = "根据ID获取单个企业的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取企业详情", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "企业不存在")
    })
    @GetMapping("/enterprises/{id}")
    public ResultVO<OrganizationVO> getEnterpriseById(@PathVariable Integer id) {
        OrganizationVO enterprise = organizationService.getEnterpriseById(id);
        return ResultVO.success("获取企业详情成功", enterprise);
    }
    
    /**
     * 上传组织Logo
     */
    @Operation(summary = "上传组织Logo", description = "上传组织的Logo图片，需要管理员权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logo上传成功",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "上传失败",
                content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "组织不存在")
    })
    @PostMapping("/organizations/{id}/logo")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@securityService.isOrganizationAdmin(#id) or hasRole('SYSADMIN')")
    public ResultVO<String> uploadOrganizationLogo(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file) {
        
        String logoUrl = organizationService.updateOrganizationLogo(id, file);
        return ResultVO.success("Logo上传成功", logoUrl);
    }
} 