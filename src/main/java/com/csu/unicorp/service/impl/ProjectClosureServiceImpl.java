package com.csu.unicorp.service.impl;

import com.csu.unicorp.dto.ProjectClosureDTO;
import com.csu.unicorp.entity.ProjectClosure;
import com.csu.unicorp.mapper.ProjectClosureMapper;
import com.csu.unicorp.service.ProjectClosureService;
import com.csu.unicorp.vo.ProjectClosureVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectClosureServiceImpl implements ProjectClosureService {
    private final ProjectClosureMapper closureMapper;
    public ProjectClosureServiceImpl(ProjectClosureMapper closureMapper) { this.closureMapper = closureMapper; }

    @Override
    public ProjectClosureVO closeProject(Integer projectId, ProjectClosureDTO dto) {
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
        ProjectClosureVO vo = new ProjectClosureVO();
        BeanUtils.copyProperties(closure, vo);
        if (closure.getAttachments() != null) {
            vo.setAttachments(List.of(closure.getAttachments().split(",")));
        }
        return vo;
    }
}
