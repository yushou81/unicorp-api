package com.csu.unicorp.controller;

import com.csu.unicorp.dto.ProjectPermissionAssignDTO;
import com.csu.unicorp.dto.ProjectPermissionRevokeDTO;
import com.csu.unicorp.service.ProjectPermissionService;
import com.csu.unicorp.vo.ProjectMemberPermissionVO;
import com.csu.unicorp.vo.ProjectPermissionLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/projects/{projectId}/permissions")
public class ProjectPermissionController {
    @Autowired
    private ProjectPermissionService projectPermissionService;

    @PostMapping("/assign")
    public Map<String, String> assignPermission(@PathVariable Integer projectId,
                                   @RequestBody ProjectPermissionAssignDTO dto,
                                   @RequestHeader("operator") String operator) {
        dto.setProjectId(projectId);
        projectPermissionService.assignPermission(dto, operator);
        return Map.of("result", "success");
    }

    @PostMapping("/revoke")
    public Map<String, String> revokePermission(@PathVariable Integer projectId,
                                   @RequestBody ProjectPermissionRevokeDTO dto,
                                   @RequestHeader("operator") String operator) {
        dto.setProjectId(projectId);
        projectPermissionService.revokePermission(dto, operator);
        return Map.of("result", "success");
    }

    @GetMapping
    public List<ProjectMemberPermissionVO> listPermissions(@PathVariable Integer projectId,
                                                           @RequestParam(required = false) Integer userId) {
        return projectPermissionService.listPermissions(projectId, userId);
    }

    @GetMapping("/logs")
    public List<ProjectPermissionLogVO> listPermissionLogs(@PathVariable Integer projectId) {
        return projectPermissionService.listPermissionLogs(projectId);
    }
} 