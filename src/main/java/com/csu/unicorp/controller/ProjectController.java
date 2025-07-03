package com.csu.unicorp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.ProjectCreationDTO;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.service.ProjectService;
import com.csu.unicorp.vo.ProjectMemberVO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.csu.unicorp.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

/**
 * 项目控制器
 */
@Tag(name = "Projects", description = "合作项目管理")
@RestController
@RequestMapping("/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserMapper userMapper;

    /**
     * 获取项目列表
     */
    @Operation(summary = "获取项目列表(公开)", description = "获取所有状态为'recruiting'的项目列表，支持分页和搜索")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取项目列表",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class)))
    })
    @GetMapping
    public ResultVO<IPage<ProjectVO>> getProjectList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer organizationId,
            @RequestParam(required = false) List<String> difficulty,
            @RequestParam(required = false) List<String> supportLanguages,
            @RequestParam(required = false) List<String> techFields,
            @RequestParam(required = false) List<String> programmingLanguages,
            @RequestParam(required = false, defaultValue = "all") String needstatus,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userMapper.findByUsername(userDetails.getUsername());
        Integer userId = user != null ? user.getId() : null;
        IPage<ProjectVO> result = projectService.getProjectList(
                page, size, keyword, organizationId, difficulty, supportLanguages, techFields, programmingLanguages, userId, needstatus);
        return ResultVO.success("获取项目列表成功", result);
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
    public ResultVO<ProjectVO> createProject(
            @Valid @RequestBody ProjectCreationDTO projectCreationDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        ProjectVO project = projectService.createProject(projectCreationDTO, userDetails);
        return ResultVO.success("项目创建成功", project);
    }

    /**
     * 获取特定项目详情
     */
    @Operation(summary = "获取特定项目详情", description = "根据ID获取项目的详细信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取项目详情",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResultVO.class))),
            @ApiResponse(responseCode = "404", description = "项目未找到")
    })
    @GetMapping("/{id}")
    public ResultVO<ProjectVO> getProject(@PathVariable Integer id) {
        ProjectVO project = projectService.getProjectById(id);
        return ResultVO.success("获取项目详情成功", project);
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
    @PreAuthorize("hasAnyRole('TEACHER', 'EN_ADMIN', 'EN_TEACHER','STUDENT')")
    public ResultVO<ProjectVO> updateProject(
            @PathVariable Integer id,
            @Valid @RequestBody ProjectCreationDTO projectCreationDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        ProjectVO project = projectService.updateProject(id, projectCreationDTO, userDetails);
        return ResultVO.success("项目更新成功", project);
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
    public ResultVO<Void> deleteProject(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        projectService.deleteProject(id, userDetails);
        return ResultVO.success("项目删除成功");
    }

    @DeleteMapping("/{projectId}/member/{userId}")
    public ResultVO<?> removeProjectMember(
            @PathVariable Integer projectId,
            @PathVariable Integer userId) {
        projectService.removeProjectMember(projectId, userId);
        return ResultVO.success("移除成功");
    }

    @GetMapping("/{projectId}/members")
    public ResultVO<List<ProjectMemberVO>> getProjectMembers(@PathVariable Integer projectId) {
        List<ProjectMemberVO> members = projectService.getProjectMembers(projectId);
        return ResultVO.success("查询成功", members);
    }
}