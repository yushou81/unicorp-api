package com.csu.unicorp.controller;

import com.csu.unicorp.service.OrganizationService;
import com.csu.unicorp.vo.OrganizationSimpleVO;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Operation(summary = "获取学校列表(公开)", description = "这是一个公开接口，用于获取所有已批准的学校列表，供学生注册时选择")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取学校列表", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = OrganizationSimpleVO.class)))
    })
    @GetMapping("/schools")
    public ResponseEntity<ResultVO<List<OrganizationSimpleVO>>> getAllSchools() {
        List<OrganizationSimpleVO> schools = organizationService.getAllSchools();
        return ResponseEntity.ok(ResultVO.success("获取学校列表成功", schools));
    }
} 