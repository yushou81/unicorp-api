package com.csu.unicorp.service.impl;

import com.csu.unicorp.dto.ProjectClosureDTO;
import com.csu.unicorp.entity.Project;
import com.csu.unicorp.entity.ProjectClosure;
import com.csu.unicorp.mapper.ProjectClosureMapper;
import com.csu.unicorp.mapper.ProjectMapper;
import com.csu.unicorp.service.ProjectClosureService;
import com.csu.unicorp.vo.ProjectClosureVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectClosureServiceImpl implements ProjectClosureService {
    private final ProjectClosureMapper closureMapper;
    
    private final ProjectMapper projectMapper;
    public ProjectClosureServiceImpl(ProjectClosureMapper closureMapper, ProjectMapper projectMapper) {
        this.closureMapper = closureMapper;
        this.projectMapper = projectMapper;
    }
    @Override
    public ProjectClosureVO closeProject(Integer projectId, ProjectClosureDTO dto) {
        Project project = projectMapper.selectById(projectId);
if (project != null) {
    project.setStatus("closed");
    projectMapper.updateById(project);
}
        ProjectClosure closure = new ProjectClosure();
        BeanUtils.copyProperties(dto, closure);
        closure.setProjectId(projectId);
        closure.setStatus("closed");
        if (dto.getAttachments() != null) {
            closure.setAttachments(String.join(",", dto.getAttachments()));
        }
        closureMapper.insert(closure);
        ProjectClosureVO vo = new ProjectClosureVO();
        BeanUtils.copyProperties(closure, vo);
        vo.setAttachments(dto.getAttachments());
        return vo;
    }

    @Override
public ProjectClosureVO getClosure(Integer projectId) {
    ProjectClosure closure = closureMapper.selectOne(
        new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ProjectClosure>().eq("project_id", projectId));
    if (closure == null) {
        // 查无数据时返回null，或者可以抛出自定义异常
        return null;
        // 或者 throw new BusinessException("未找到该项目的结项信息");
    }
    ProjectClosureVO vo = new ProjectClosureVO();
    BeanUtils.copyProperties(closure, vo);
    if (closure.getAttachments() != null) {
        vo.setAttachments(List.of(closure.getAttachments().split(",")));
    }
    return vo;
}
}
