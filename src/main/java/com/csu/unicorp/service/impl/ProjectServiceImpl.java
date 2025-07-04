package com.csu.unicorp.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;

import com.csu.unicorp.dto.ProjectCreationDTO;
import com.csu.unicorp.entity.organization.Organization;
import com.csu.unicorp.entity.Project;
import com.csu.unicorp.entity.ProjectApplication;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.mapper.ProjectApplicationMapper;
import com.csu.unicorp.mapper.ProjectMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.ProjectService;
import com.csu.unicorp.vo.PageResultVO;
import com.csu.unicorp.vo.ProjectVO;
import org.springframework.beans.BeanUtils;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.Arrays;
import java.util.List;
import com.csu.unicorp.common.exception.BusinessException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private ProjectApplicationMapper applicationMapper;

    public ProjectServiceImpl(ProjectMapper projectMapper) { this.projectMapper = projectMapper; }

    @Override
    public ProjectVO createProject(ProjectCreationDTO dto) {
        Project project;
        if (dto.getProjectId() != null) {
            // 编辑草稿或正式发布，先查原项目
            project = projectMapper.selectById(dto.getProjectId());
            if (project == null) throw new BusinessException("项目不存在");
            // 用最新数据覆盖
            BeanUtils.copyProperties(dto, project);
            if (dto.getAttachments() != null) {
                project.setAttachments(String.join(",", dto.getAttachments()));
            }
            // status直接用前端传入的
            project.setStatus(dto.getStatus());
            projectMapper.updateById(project);
        } else {
            // 新建
            project = new Project();
            BeanUtils.copyProperties(dto, project);
            if (dto.getAttachments() != null) {
                project.setAttachments(String.join(",", dto.getAttachments()));
            }
            // status直接用前端传入的
            project.setStatus(dto.getStatus());
            projectMapper.insert(project);
        }
        ProjectVO vo = new ProjectVO();
        BeanUtils.copyProperties(project, vo);
        vo.setAttachments(dto.getAttachments());
        return vo;
    }

    @Override
public ProjectVO updateProject(ProjectCreationDTO dto) {
    if (dto.getProjectId() == null) {
        throw new BusinessException("projectId不能为空");
    }
    Project project = projectMapper.selectById(dto.getProjectId());
    if (project == null) {
        throw new BusinessException("项目不存在");
    }
    BeanUtils.copyProperties(dto, project, "id");
    if (dto.getAttachments() != null) {
        project.setAttachments(String.join(",", dto.getAttachments()));
    }
    projectMapper.updateById(project);

    ProjectVO vo = new ProjectVO();
    BeanUtils.copyProperties(project, vo);
    vo.setAttachments(dto.getAttachments());
    return vo;
}

    public PageResultVO<ProjectVO> getProjectList(String status, String initiatorType, String field, String keyword, Integer initiatorId, Integer organizationId, int page, int pageSize,Integer userId) {
        QueryWrapper<Project> wrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        if (initiatorType != null && !initiatorType.isEmpty()) {
            wrapper.eq("initiator_type", initiatorType);
        }
        if (field != null && !field.isEmpty()) {
            List<String> fieldList = Arrays.asList(field.split(","));
            wrapper.in("field", fieldList);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("title", keyword).or().like("description", keyword));
        }
    
        // 组织优先
        if (organizationId != null) {
            List<User> users = userMapper.selectList(new QueryWrapper<User>().eq("organization_id", organizationId));
            List<Integer> userIds = users.stream().map(User::getId).collect(Collectors.toList());
            if (!userIds.isEmpty()) {
                wrapper.in("initiator_id", userIds);
            } else {
                // 没有用户，直接返回空分页
                return new PageResultVO<>(0, Collections.emptyList());
            }
        } else if (initiatorId != null) {
            wrapper.eq("initiator_id", initiatorId);
        }
    
        // 分页查询
        Page<Project> pageObj = new Page<>(page, pageSize);
        Page<Project> projectPage = projectMapper.selectPage(pageObj, wrapper);
    
        List<ProjectVO> voList = projectPage.getRecords().stream().map(project -> {
            ProjectVO vo = new ProjectVO();
            BeanUtils.copyProperties(project, vo);
            if (project.getAttachments() != null) {
                vo.setAttachments(List.of(project.getAttachments().split(",")));
            }
            // 查询组织名称
            if (project.getInitiatorId() != null) {
                User user = userMapper.selectById(project.getInitiatorId());
                if (user != null && user.getOrganizationId() != null) {
                    Organization org = organizationMapper.selectById(user.getOrganizationId());
                    if (org != null) {
                        vo.setOrganizationName(org.getOrganizationName());
                    }
                }
            }
            // 查询当前用户是否已申请
            if (userId != null) {
                ProjectApplication application = applicationMapper.selectOne(
                    new QueryWrapper<ProjectApplication>()
                        .eq("project_id", project.getProjectId())
                        .eq("applicant_id", userId)
                );
                if (application != null) {
                    vo.setHasApplied(true);
                    vo.setApplicationStatus(application.getStatus());
                } else {
                    vo.setHasApplied(false);
                    vo.setApplicationStatus(null);
                }
            } else {
                vo.setHasApplied(false);
                vo.setApplicationStatus(null);
            }
            return vo;
        }).collect(Collectors.toList());
    
        return new PageResultVO<>(projectPage.getTotal(), voList);
    }


    @Override
    public void updateProjectStatus(Integer projectId, String status,String reason) {
        Project project = projectMapper.selectById(projectId);
        if (project == null) {
            throw new BusinessException("项目不存在");
        }
        project.setStatus(status);
        project.setReason(reason);
        projectMapper.updateById(project);
    }


    @Override
    public ProjectVO getProjectById(Integer projectId) {
        Project project = projectMapper.selectById(projectId);
        ProjectVO vo = new ProjectVO();
        BeanUtils.copyProperties(project, vo);
        if (project.getAttachments() != null) {
            vo.setAttachments(List.of(project.getAttachments().split(",")));
        }
        return vo;
    }
}
