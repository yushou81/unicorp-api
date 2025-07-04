package com.csu.unicorp.service;

import com.csu.unicorp.dto.ProjectProgressDTO;
import com.csu.unicorp.vo.ProjectProgressVO;
import java.util.List;

/**
 * 项目进度服务接口
 */
public interface ProjectProgressService {
    ProjectProgressVO addProgress(Integer projectId, ProjectProgressDTO dto);
    List<ProjectProgressVO> getProgressList(Integer projectId);
}
