package com.csu.unicorp.service;

import com.csu.unicorp.dto.ProjectPermissionAssignDTO;
import com.csu.unicorp.dto.ProjectPermissionRevokeDTO;
import com.csu.unicorp.vo.ProjectMemberPermissionVO;
import com.csu.unicorp.vo.ProjectPermissionLogVO;
import java.util.List;

public interface ProjectPermissionService {
    void assignPermission(ProjectPermissionAssignDTO dto, String operator);
    void revokePermission(ProjectPermissionRevokeDTO dto, String operator);
    List<ProjectMemberPermissionVO> listPermissions(Integer projectId, Integer userId);
    List<ProjectPermissionLogVO> listPermissionLogs(Integer projectId);
} 