package com.csu.unicorp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.CourseResourceDTO;
import com.csu.unicorp.entity.course.CourseResource.ResourceType;
import com.csu.unicorp.service.CourseResourceService;
import com.csu.unicorp.vo.CourseResourceVO;
import com.csu.unicorp.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 课程资源控制器
 */
@RestController
@RequestMapping("/v1/course-resources")
@RequiredArgsConstructor
@Tag(name = "课程资源", description = "课程资源API，提供资源上传、下载、查询等功能")
public class CourseResourceController {

    private final CourseResourceService resourceService;
    
    /**
     * 上传课程资源
     */
    @Operation(summary = "上传课程资源", description = "上传课程资源文件，需要教师或企业导师权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "资源上传成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "参数错误或权限不足", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'EN_TEACHER')")
    public ResultVO<CourseResourceVO> uploadResource(
            @RequestPart("file") MultipartFile file,
            @RequestParam("courseId") Integer courseId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("resourceType") String resourceType,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        // 验证资源类型是否有效
        try {
            ResourceType.valueOf(resourceType);
        } catch (IllegalArgumentException e) {
            return ResultVO.error("无效的资源类型，可选值：document, video, code, other");
        }
        
        // 创建CourseResourceDTO对象
        CourseResourceDTO resourceDTO = new CourseResourceDTO();
        resourceDTO.setCourseId(courseId);
        resourceDTO.setTitle(title);
        resourceDTO.setDescription(description);
        resourceDTO.setResourceType(resourceType);
        
        CourseResourceVO resource = resourceService.uploadResource(file, resourceDTO, userDetails);
        return ResultVO.success("资源上传成功", resource);
    }
    
    /**
     * 删除课程资源
     */
    @Operation(summary = "删除课程资源", description = "删除指定的课程资源，需要资源上传者或教师权限")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "资源删除成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "400", description = "权限不足", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "资源不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @DeleteMapping("/{resourceId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'EN_TEACHER')")
    public ResultVO<Void> deleteResource(
            @PathVariable Integer resourceId,
            @AuthenticationPrincipal UserDetails userDetails) {
        resourceService.deleteResource(resourceId, userDetails);
        return ResultVO.success("资源删除成功");
    }
    
    /**
     * 获取课程资源详情
     */
    @Operation(summary = "获取课程资源详情", description = "根据ID获取课程资源详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取资源详情成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "资源不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/{resourceId}")
    public ResultVO<CourseResourceVO> getResourceById(@PathVariable Integer resourceId) {
        CourseResourceVO resource = resourceService.getResourceById(resourceId);
        return ResultVO.success("获取资源详情成功", resource);
    }
    
    /**
     * 获取课程资源列表
     */
    @Operation(summary = "获取课程资源列表", description = "获取指定课程的所有资源")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取资源列表成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class))),
        @ApiResponse(responseCode = "404", description = "课程不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/course/{courseId}")
    public ResultVO<IPage<CourseResourceVO>> getResourcesByCourseId(
            @PathVariable Integer courseId,
            @Parameter(description = "页码，从1开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        IPage<CourseResourceVO> resources = resourceService.getResourcesByCourseId(courseId, page, size);
        return ResultVO.success("获取资源列表成功", resources);
    }
    
    /**
     * 下载课程资源
     */
    @Operation(summary = "下载课程资源", description = "下载指定的课程资源文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "文件下载成功"),
        @ApiResponse(responseCode = "404", description = "资源不存在", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping("/download/{resourceId}")
    public ResponseEntity<Resource> downloadResource(@PathVariable Integer resourceId) {
        try {
            // 获取资源文件路径
            String filePath = resourceService.downloadResource(resourceId);
            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());
            
            // 获取资源详情，用于设置文件名
            CourseResourceVO resourceVO = resourceService.getResourceById(resourceId);
            String filename = resourceVO.getTitle() + getFileExtension(filePath);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(resourceVO.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filePath.substring(dotIndex);
    }
} 