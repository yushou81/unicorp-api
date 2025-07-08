package com.csu.unicorp.service.impl;

import com.csu.unicorp.dto.ProjectProgressDTO;
import com.csu.unicorp.entity.ProjectProgress;
import com.csu.unicorp.mapper.ProjectProgressMapper;
import com.csu.unicorp.service.ProjectProgressService;
import com.csu.unicorp.vo.ProjectProgressVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectProgressServiceImpl implements ProjectProgressService {
    private final ProjectProgressMapper progressMapper;
    public ProjectProgressServiceImpl(ProjectProgressMapper progressMapper) { this.progressMapper = progressMapper; }

    @Override
    public ProjectProgressVO addProgress(Integer projectId, ProjectProgressDTO dto) {
        ProjectProgress progress = new ProjectProgress();
        BeanUtils.copyProperties(dto, progress);
        progress.setProjectId(projectId);
        if (dto.getAttachments() != null) {
            progress.setAttachments(String.join(",", dto.getAttachments()));
        }
        progressMapper.insert(progress);
        ProjectProgressVO vo = new ProjectProgressVO();
        BeanUtils.copyProperties(progress, vo);
        vo.setAttachments(dto.getAttachments());
        return vo;
    }

    @Override
    public List<ProjectProgressVO> getProgressList(Integer projectId) {
        List<ProjectProgress> list = progressMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ProjectProgress>().eq("project_id", projectId));
        return list.stream().map(progress -> {
            ProjectProgressVO vo = new ProjectProgressVO();
            BeanUtils.copyProperties(progress, vo);
            if (progress.getAttachments() != null) {
                vo.setAttachments(List.of(progress.getAttachments().split(",")));
            }
            return vo;
        }).collect(Collectors.toList());
    }
}
