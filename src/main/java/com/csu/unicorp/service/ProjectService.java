package com.csu.unicorp.service;

import com.csu.unicorp.dto.ProjectCreationDTO;
import com.csu.unicorp.vo.PageResultVO;
import com.csu.unicorp.vo.ProjectVO;
import java.util.List;

public interface ProjectService {
    ProjectVO createProject(ProjectCreationDTO dto);
    ProjectVO getProjectById(Integer projectId);
    PageResultVO<ProjectVO> getProjectList(String status, String initiatorType, String field, String keyword, Integer initiatorId,Integer organizationId, int page, int pageSize,Integer userId);
    
    PageResultVO<ProjectVO> getMyProjectList(String status, String initiatorType, String field, String keyword, Integer initiatorId,Integer organizationId, int page, int pageSize,Integer userId);
    
    void updateProjectStatus(Integer projectId, String status,String reason);

    ProjectVO updateProject(ProjectCreationDTO dto);
}