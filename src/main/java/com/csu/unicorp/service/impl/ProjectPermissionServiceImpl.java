package com.csu.unicorp.service.impl;

import com.csu.unicorp.dto.ProjectPermissionAssignDTO;
import com.csu.unicorp.dto.ProjectPermissionRevokeDTO;
import com.csu.unicorp.entity.ProjectMemberPermission;
import com.csu.unicorp.entity.ProjectPermissionLog;
import com.csu.unicorp.mapper.ProjectMemberPermissionMapper;
import com.csu.unicorp.mapper.ProjectPermissionLogMapper;
import com.csu.unicorp.service.ProjectPermissionService;
import com.csu.unicorp.vo.ProjectMemberPermissionVO;
import com.csu.unicorp.vo.ProjectPermissionLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectPermissionServiceImpl implements ProjectPermissionService {
    @Autowired
    private ProjectMemberPermissionMapper memberPermissionMapper;
    @Autowired
    private ProjectPermissionLogMapper permissionLogMapper;

    @Override
    public void assignPermission(ProjectPermissionAssignDTO dto, String operator) {
        for (String role : dto.getRoles()) {
            ProjectMemberPermission p = new ProjectMemberPermission();
            p.setProjectId(dto.getProjectId());
            p.setUserId(dto.getUserId());
            p.setRole(role);
            p.setExpireAt(dto.getExpireAt());
            p.setAssignedAt(new Date());
            memberPermissionMapper.insert(p);

            ProjectPermissionLog log = new ProjectPermissionLog();
            log.setProjectId(dto.getProjectId());
            log.setUserId(dto.getUserId());
            log.setAction("assign");
            log.setRole(role);
            log.setOperator(operator);
            log.setTime(new Date());
            log.setExpireAt(dto.getExpireAt());
            permissionLogMapper.insert(log);
        }
    }

    @Override
    public void revokePermission(ProjectPermissionRevokeDTO dto, String operator) {
        for (String role : dto.getRoles()) {
            memberPermissionMapper.deleteByProjectIdAndUserIdAndRole(dto.getProjectId(), dto.getUserId(), role);
            ProjectPermissionLog log = new ProjectPermissionLog();
            log.setProjectId(dto.getProjectId());
            log.setUserId(dto.getUserId());
            log.setAction("revoke");
            log.setRole(role);
            log.setOperator(operator);
            log.setTime(new Date());
            permissionLogMapper.insert(log);
        }
    }

    @Override
    public List<ProjectMemberPermissionVO> listPermissions(Integer projectId, Integer userId) {
        List<ProjectMemberPermission> list = (userId == null)
            ? memberPermissionMapper.selectByProjectId(projectId)
            : memberPermissionMapper.selectByProjectIdAndUserId(projectId, userId);
        Map<Integer, ProjectMemberPermissionVO> map = new HashMap<>();
        for (ProjectMemberPermission p : list) {
            ProjectMemberPermissionVO vo = map.getOrDefault(p.getUserId(), new ProjectMemberPermissionVO());
            vo.setUserId(p.getUserId());
            if (vo.getRoles() == null) vo.setRoles(new ArrayList<>());
            vo.getRoles().add(p.getRole());
            vo.setExpireAt(p.getExpireAt());
            map.put(p.getUserId(), vo);
        }
        return new ArrayList<>(map.values());
    }

    @Override
    public List<ProjectPermissionLogVO> listPermissionLogs(Integer projectId) {
        List<ProjectPermissionLog> logs = permissionLogMapper.selectByProjectId(projectId);
        return logs.stream().map(log -> {
            ProjectPermissionLogVO vo = new ProjectPermissionLogVO();
            vo.setLogId(log.getLogId());
            vo.setUserId(log.getUserId());
            vo.setAction(log.getAction());
            vo.setRoles(Collections.singletonList(log.getRole()));
            vo.setOperator(log.getOperator());
            vo.setTime(log.getTime());
            vo.setExpireAt(log.getExpireAt());
            return vo;
        }).collect(Collectors.toList());
    }
} 