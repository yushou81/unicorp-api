package com.csu.unicorp.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.ResourceCreationDTO;
import com.csu.unicorp.service.FileService;
import com.csu.unicorp.service.ResourceService;
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
import lombok.extern.slf4j.Slf4j;

/**
 * 资源管理控制器
 */
@Slf4j
@Tag(name = "Resources", description = "资源共享管理")
@RestController
@RequestMapping("/v1/resources")
@RequiredArgsConstructor
public class ResourceController {
    
    private final ResourceService resourceService;
    private final FileService fileService;
    
    /**
     * 获取资源列表
     */
    @Operation(summary = "获取资源列表 (公开)", description = "获取所有已发布的资源列表，支持分页和搜索")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取资源列表", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping
    public ResultVO<IPage<ResourceVO>> getResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        
        IPage<ResourceVO> resources = resourceService.getResources(page, size, keyword);
        return ResultVO.success("获取资源列表成功", resources);
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
    @PostMapping("/")
    public ResponseEntity<ResultVO<ResourceVO>> createResource(
            @Valid @RequestBody ResourceCreationDTO resourceDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        ResourceVO resource = resourceService.createResource(resourceDTO, userDetails);
        return new ResponseEntity<>(ResultVO.success("资源创建成功", resource), HttpStatus.CREATED);
    }
    
    // /**
    //  * 更新资源
    //  */
    // @Operation(summary = "[所有者] 更新资源信息", description = "由资源所有者调用，用于更新资源信息")
    // @ApiResponses(value = {
    //     @ApiResponse(responseCode = "200", description = "资源更新成功", 
    //             content = @Content(mediaType = "application/json", 
    //             schema = @Schema(implementation = ResourceVO.class))),
    //     @ApiResponse(responseCode = "403", description = "权限不足"),
    //     @ApiResponse(responseCode = "404", description = "资源未找到")
    // })
    // @PutMapping("/{id}")
    // public ResponseEntity<ResultVO<ResourceVO>> updateResource(
    //         @PathVariable Integer id,
    //         @Valid @RequestBody ResourceCreationDTO resourceDTO,
    //         @AuthenticationPrincipal UserDetails userDetails) {
        
    //     ResourceVO resource = resourceService.updateResource(id, resourceDTO, userDetails);
    //     return ResponseEntity.ok(ResultVO.success("资源更新成功", resource));
    // }
    
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
    
    /**
     * 上传资源文件并创建资源
     */
    @Operation(summary = "[教师/导师] 上传资源文件并创建资源", description = "一步完成文件上传和资源创建")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "资源创建成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResourceVO.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultVO<ResourceVO>> uploadAndCreateResource(
            @RequestParam("title") String title,
            @RequestParam("resourceType") String resourceType,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "visibility", required = false) String visibility,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            // 1. 上传文件
            log.info("正在上传资源文件: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
            String fileUrl = fileService.uploadFile(file, "resource");
            log.info("文件上传成功，URL: {}", fileUrl);
            
            // 2. 创建资源DTO
            ResourceCreationDTO resourceDTO = new ResourceCreationDTO();
            resourceDTO.setTitle(title);
            resourceDTO.setResourceType(resourceType);
            resourceDTO.setDescription(description);
            resourceDTO.setFileUrl(fileUrl);
            resourceDTO.setVisibility(visibility);
            
            // 3. 创建资源
            ResourceVO resource = resourceService.createResource(resourceDTO, userDetails);
            log.info("资源创建成功，ID: {}", resource.getId());
            
            return ResponseEntity.ok(ResultVO.success("资源上传成功", resource));
        } catch (Exception e) {
            log.error("资源上传失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultVO.error("资源上传失败: " + e.getMessage()));
        }
    }
    
    /**
     * 上传文件并更新资源
     */
    @Operation(summary = "[所有者] 上传文件并更新资源", description = "一步完成文件上传和资源更新")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "资源更新成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResourceVO.class))),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "资源未找到")
    })
    @PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultVO<ResourceVO>> uploadAndUpdateResource(
            @PathVariable Integer id,
            @RequestParam(value = "title" , required = false) String title,
            @RequestParam(value = "resourceType" , required = false) String resourceType,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "visibility", required = false) String visibility,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            // 0. 获取现有资源信息，确保资源存在
            ResourceVO existingResource = resourceService.getResourceById(id);
            
            // 1. 创建资源DTO
            ResourceCreationDTO resourceDTO = new ResourceCreationDTO();
            resourceDTO.setTitle(title);
            resourceDTO.setResourceType(resourceType);
            resourceDTO.setDescription(description);
            resourceDTO.setVisibility(visibility);
            
            // 2. 如果提供了新文件，则上传并更新fileUrl
            if (file != null && !file.isEmpty()) {
                log.info("正在上传更新的资源文件: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
                String fileUrl = fileService.uploadFile(file, "resource");
                log.info("文件上传成功，URL: {}", fileUrl);
                resourceDTO.setFileUrl(fileUrl);
            } else {
                // 如果没有提供新文件，则保留原来的fileUrl
                resourceDTO.setFileUrl(existingResource.getFileUrl());
                log.info("未提供新文件，保留原有文件URL: {}", existingResource.getFileUrl());
            }
            
            // 3. 更新资源
            ResourceVO resource = resourceService.updateResource(id, resourceDTO, userDetails);
            log.info("资源更新成功，ID: {}", resource.getId());
            
            return ResponseEntity.ok(ResultVO.success("资源更新成功", resource));
        } catch (Exception e) {
            log.error("资源更新失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResultVO.error("资源更新失败: " + e.getMessage()));
        }
    }

    /**
     * 下载资源文件
     */
    @Operation(summary = "下载资源文件", description = "下载指定ID的资源文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "文件下载成功"),
        @ApiResponse(responseCode = "404", description = "资源未找到")
    })
    @GetMapping("/{id}/download")
    public ResponseEntity<org.springframework.core.io.Resource> downloadResource(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            // 获取资源详情
            ResourceVO resourceVO = resourceService.getResourceById(id);
            
            // 记录下载行为（可选）
            log.info("用户 {} 下载资源: {}", userDetails != null ? userDetails.getUsername() : "匿名用户", resourceVO.getTitle());
            
            // 从文件服务获取文件资源
            String fileUrl = resourceVO.getFileUrl();
            org.springframework.core.io.Resource fileResource = fileService.loadFileAsResource(fileUrl);
            
            // 提取文件名
            String filename = fileService.getFilenameFromUrl(fileUrl);
            
            // 设置响应头
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(fileResource);
        } catch (Exception e) {
            log.error("文件下载失败", e);
            return ResponseEntity.notFound().build();
        }
    }
} 