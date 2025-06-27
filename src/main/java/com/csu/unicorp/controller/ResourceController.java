package com.csu.unicorp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.ResourceCreationDTO;
import com.csu.unicorp.service.ResourceService;
import com.csu.unicorp.vo.PageResultVO;
import com.csu.unicorp.vo.ResourceVO;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 资源管理控制器
 */
@Tag(name = "Resources", description = "资源共享管理")
@RestController
@RequestMapping("/v1/resources")
@RequiredArgsConstructor
public class ResourceController {
    
    private final ResourceService resourceService;
    
    /**
     * 获取资源列表
     */
    @Operation(summary = "获取资源列表 (公开)", description = "获取所有已发布的资源列表，支持分页和搜索")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取资源列表", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = PageResultVO.class)))
    })
    @GetMapping
    public ResponseEntity<ResultVO<PageResultVO<ResourceVO>>> getResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        
        IPage<ResourceVO> resources = resourceService.getResources(page, size, keyword);
        PageResultVO<ResourceVO> pageResult = new PageResultVO<>(resources);
        
        return ResponseEntity.ok(ResultVO.success("获取资源列表成功", pageResult));
    }
    
    /**
     * 获取资源详情
     */
    @Operation(summary = "获取特定资源详情", description = "根据ID获取资源的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取资源详情", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResourceVO.class))),
        @ApiResponse(responseCode = "404", description = "资源未找到")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResultVO<ResourceVO>> getResourceById(@PathVariable Integer id) {
        ResourceVO resource = resourceService.getResourceById(id);
        return ResponseEntity.ok(ResultVO.success("获取资源详情成功", resource));
    }
    
    /**
     * 创建资源
     */
    @Operation(summary = "[教师/导师] 上传新资源", description = "由教师或企业导师调用，用于发布一个新的共享资源")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "资源创建成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResourceVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping
    public ResponseEntity<ResultVO<ResourceVO>> createResource(
            @Valid @RequestBody ResourceCreationDTO resourceDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        ResourceVO resource = resourceService.createResource(resourceDTO, userDetails);
        return new ResponseEntity<>(ResultVO.success("资源创建成功", resource), HttpStatus.CREATED);
    }
    
    /**
     * 更新资源
     */
    @Operation(summary = "[所有者] 更新资源信息", description = "由资源所有者调用，用于更新资源信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "资源更新成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResourceVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "资源未找到")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResultVO<ResourceVO>> updateResource(
            @PathVariable Integer id,
            @Valid @RequestBody ResourceCreationDTO resourceDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        ResourceVO resource = resourceService.updateResource(id, resourceDTO, userDetails);
        return ResponseEntity.ok(ResultVO.success("资源更新成功", resource));
    }
    
    /**
     * 删除资源
     */
    @Operation(summary = "[所有者] 删除资源", description = "由资源所有者调用，用于删除资源")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "资源未找到")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        resourceService.deleteResource(id, userDetails);
        return ResponseEntity.noContent().build();
    }
} 