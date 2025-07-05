package com.csu.unicorp.service.impl;

import com.csu.unicorp.entity.ProjectLog;
import com.csu.unicorp.mapper.ProjectLogMapper;
import com.csu.unicorp.service.ProjectLogService;
import com.csu.unicorp.vo.ProjectLogVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectLogServiceImpl implements ProjectLogService {
    private final ProjectLogMapper logMapper;
    public ProjectLogServiceImpl(ProjectLogMapper logMapper) { this.logMapper = logMapper; }

    @Override
    public List<ProjectLogVO> getProjectLogs(Integer projectId) {
        List<ProjectLog> list = logMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ProjectLog>().eq("project_id", projectId));
        return list.stream().map(log -> {
            ProjectLogVO vo = new ProjectLogVO();
            BeanUtils.copyProperties(log, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
