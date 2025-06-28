package com.csu.unicorp.controller;

import com.csu.unicorp.service.OrganizationService;
import com.csu.unicorp.vo.OrganizationSimpleVO;
import com.csu.unicorp.vo.OrganizationVO;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/schools")
    public ResponseEntity<ResultVO<List<?>>> getAllSchools(
            @Parameter(description = "视图类型，决定返回数据的详细程度", schema = @Schema(type = "string", allowableValues = {"simple", "detailed"}, defaultValue = "simple"))
            @RequestParam(required = false, defaultValue = "simple") String view) {
        
        List<?> schools = organizationService.getAllSchools(view);
        return ResponseEntity.ok(ResultVO.success("获取学校列表成功", schools));
    }
    
    /**
     * 获取单个学校详情
     */
    @Operation(summary = "获取学校详情(公开)", description = "根据ID获取单个学校的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取学校详情", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrganizationVO.class))),
        @ApiResponse(responseCode = "404", description = "学校不存在")
    })
    @GetMapping("/schools/{id}")
    public ResponseEntity<ResultVO<OrganizationVO>> getSchoolById(@PathVariable Integer id) {
        OrganizationVO school = organizationService.getSchoolById(id);
        return ResponseEntity.ok(ResultVO.success("获取学校详情成功", school));
    }
    
    /**
     * 获取所有企业列表
     */
    @Operation(summary = "获取企业列表(公开)", description = "获取所有已批准的企业列表。可通过`view`参数控制返回数据的详细程度。")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取企业列表", 
                content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/enterprises")
    public ResponseEntity<ResultVO<List<?>>> getAllEnterprises(
            @Parameter(description = "视图类型，决定返回数据的详细程度", schema = @Schema(type = "string", allowableValues = {"simple", "detailed"}, defaultValue = "simple"))
            @RequestParam(required = false, defaultValue = "simple") String view) {
        
        List<?> enterprises = organizationService.getAllEnterprises(view);
        return ResponseEntity.ok(ResultVO.success("获取企业列表成功", enterprises));
    }
    
    /**
     * 获取单个企业详情
     */
    @Operation(summary = "获取企业详情(公开)", description = "根据ID获取单个企业的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取企业详情", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrganizationVO.class))),
        @ApiResponse(responseCode = "404", description = "企业不存在")
    })
    @GetMapping("/enterprises/{id}")
    public ResponseEntity<ResultVO<OrganizationVO>> getEnterpriseById(@PathVariable Integer id) {
        OrganizationVO enterprise = organizationService.getEnterpriseById(id);
        return ResponseEntity.ok(ResultVO.success("获取企业详情成功", enterprise));
    }
} 