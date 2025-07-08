package com.csu.unicorp.service;

import com.csu.unicorp.dto.ProjectClosureDTO;
import com.csu.unicorp.vo.ProjectClosureVO;

/**
 * 项目结项服务接口
 */
public interface ProjectClosureService {
    ProjectClosureVO closeProject(Integer projectId, ProjectClosureDTO dto);
    ProjectClosureVO getClosure(Integer projectId);
}
