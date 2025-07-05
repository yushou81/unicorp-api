package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.csu.unicorp.dto.ProjectApplicationDTO;
import com.csu.unicorp.dto.ProjectApplicationReviewDTO;
import com.csu.unicorp.entity.Project;
import com.csu.unicorp.entity.ProjectApplication;


import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.organization.Organization;

import com.csu.unicorp.mapper.ProjectApplicationMapper;
import com.csu.unicorp.mapper.ProjectMapper;
import com.csu.unicorp.service.ProjectApplicationService;
import com.csu.unicorp.vo.ProjectApplicationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.mapper.OrganizationMapper;

import com.csu.unicorp.entity.User;


import com.csu.unicorp.service.ProjectApplicationService;
import com.csu.unicorp.service.ProjectService;
import com.csu.unicorp.vo.MyProjectApplicationDetailVO;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

@Service
public class ProjectApplicationServiceImpl implements ProjectApplicationService {
    @Autowired
    private  ProjectApplicationMapper applicationMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private OrganizationMapper organizationMapper;
    @Override
    public ProjectApplicationVO applyForProject(Integer projectId, ProjectApplicationDTO dto) {
        ProjectApplication app = new ProjectApplication();
        BeanUtils.copyProperties(dto, app);
        app.setProjectId(projectId);
        app.setStatus("pending");
        applicationMapper.insert(app);
        ProjectApplicationVO vo = new ProjectApplicationVO();
        BeanUtils.copyProperties(app, vo);
        return vo;
    }

    @Override
public ProjectApplicationVO reviewApplication(Integer projectId, Integer applicationId, ProjectApplicationReviewDTO dto) {
    ProjectApplication app = applicationMapper.selectById(applicationId);
    if ("approved".equals(dto.getStatus())) {
        app.setApprovedTime(new Timestamp(System.currentTimeMillis()));
        // 同步更改项目状态为active
        Project project = projectMapper.selectById(app.getProjectId());
        if (project != null) {
            project.setStatus("matched");
            projectMapper.updateById(project);
        }
    }
    app.setStatus(dto.getStatus());
    applicationMapper.updateById(app);
    ProjectApplicationVO vo = new ProjectApplicationVO();
    BeanUtils.copyProperties(app, vo);
    return vo;
}

    @Override
public List<ProjectApplicationVO> getProjectApplications(Integer projectId) {
    List<ProjectApplication> list = applicationMapper.selectList(
        new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ProjectApplication>().eq("project_id", projectId));
    return list.stream().map(app -> {
        ProjectApplicationVO vo = new ProjectApplicationVO();
        BeanUtils.copyProperties(app, vo);
        // 查询申请人所属组织
        User user = userMapper.selectById(app.getApplicantId());
        if (user != null && user.getOrganizationId() != null) {
            Organization org = organizationMapper.selectById(user.getOrganizationId());
            if (org != null) {
                vo.setOrganizationName(org.getOrganizationName());
            }
        }
        vo.setCreateTime(app.getCreateTime());
        return vo;
    }).collect(Collectors.toList());
}



public List<ProjectApplicationVO> getMyProjectApplications(Integer applicantId) {
    List<ProjectApplication> list = applicationMapper.selectList(
        new QueryWrapper<ProjectApplication>().eq("applicant_id", applicantId));
        return list.stream().map(app -> {
            ProjectApplicationVO vo = new ProjectApplicationVO();
            BeanUtils.copyProperties(app, vo);
            // 组织名、时间等补充
            User user = userMapper.selectById(app.getApplicantId());
            if (user != null && user.getOrganizationId() != null) {
                Organization org = organizationMapper.selectById(user.getOrganizationId());
                if (org != null) {
                    vo.setOrganizationName(org.getOrganizationName());
                }
            }
            vo.setCreateTime(app.getCreateTime());
            vo.setApproveTime(app.getApprovedTime());
            // 查项目信息
            Project project = projectMapper.selectById(app.getProjectId());
            if (project != null) {
                vo.setProjectName(project.getTitle());
                vo.setProjectDescription(project.getDescription());
            }
            return vo;
        }).collect(Collectors.toList());
}

}
