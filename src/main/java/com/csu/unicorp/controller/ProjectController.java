package com.csu.unicorp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.ProjectCreationDTO;
import com.csu.unicorp.service.ProjectService;
import com.csu.unicorp.vo.ProjectVO;
import com.csu.unicorp.vo.ResultVO;
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
 * 项目控制器
 */
@Tag(name = "Projects", description = "合作项目管理")
@RestController
@RequestMapping("/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectService projectService;
    
    /**
     * 获取项目列表
     */
    @Operation(summary = "获取项目列表(公开)", description = "获取所有状态为'recruiting'的项目列表，支持分页和搜索")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取项目列表", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ProjectVO.class)))
    })
    @GetMapping
    public ResponseEntity<ResultVO<IPage<ProjectVO>>> getProjects(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        IPage<ProjectVO> projects = projectService.getProjectList(page, size, keyword);
        return ResponseEntity.ok(ResultVO.success("获取项目列表成功", projects));
    }
    
    /**
     * 创建新项目
     */
    @Operation(summary = "[校/企] 创建新项目", description = "由教师或企业用户调用，用于发布一个新的合作项目")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "项目创建成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ProjectVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('TEACHER', 'EN_ADMIN', 'EN_TEACHER')")
    public ResponseEntity<ResultVO<ProjectVO>> createProject(
            @Valid @RequestBody ProjectCreationDTO projectCreationDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        ProjectVO project = projectService.createProject(projectCreationDTO, userDetails);
        return new ResponseEntity<>(ResultVO.success("项目创建成功", project), HttpStatus.CREATED);
    }
    
    /**
     * 获取特定项目详情
     */
    @Operation(summary = "获取特定项目详情", description = "根据ID获取项目的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功获取项目详情", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ProjectVO.class))),
        @ApiResponse(responseCode = "404", description = "项目未找到")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResultVO<ProjectVO>> getProject(@PathVariable Integer id) {
        ProjectVO project = projectService.getProjectById(id);
        return ResponseEntity.ok(ResultVO.success("获取项目详情成功", project));
    }
    
    /**
     * 更新项目信息
     */
    @Operation(summary = "[所有者] 更新项目信息", description = "更新已发布项目的信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "项目更新成功", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = ProjectVO.class))),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "项目未找到")
    })
    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('TEACHER', 'EN_ADMIN', 'EN_TEACHER')")
    public ResponseEntity<ResultVO<ProjectVO>> updateProject(
            @PathVariable Integer id,
            @Valid @RequestBody ProjectCreationDTO projectCreationDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        ProjectVO project = projectService.updateProject(id, projectCreationDTO, userDetails);
        return ResponseEntity.ok(ResultVO.success("项目更新成功", project));
    }
    
    /**
     * 删除项目
     */
    @Operation(summary = "[所有者] 删除项目", description = "逻辑删除一个项目")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "删除成功"),
        @ApiResponse(responseCode = "403", description = "权限不足"),
        @ApiResponse(responseCode = "404", description = "项目未找到")
    })
    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('TEACHER', 'EN_ADMIN', 'EN_TEACHER')")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        projectService.deleteProject(id, userDetails);
        return ResponseEntity.noContent().build();
    }
} 