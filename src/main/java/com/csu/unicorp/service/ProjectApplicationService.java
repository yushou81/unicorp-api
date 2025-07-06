package com.csu.unicorp.service;

import com.csu.unicorp.dto.ProjectApplicationDTO;
import com.csu.unicorp.dto.ProjectApplicationReviewDTO;
import com.csu.unicorp.vo.ProjectApplicationVO;
import java.util.List;

/**
 * 项目对接/合作申请服务接口
 */
public interface ProjectApplicationService {
    ProjectApplicationVO applyForProject(Integer projectId, ProjectApplicationDTO dto);
    ProjectApplicationVO reviewApplication(Integer projectId, Integer applicationId, ProjectApplicationReviewDTO dto);
    List<ProjectApplicationVO> getProjectApplications(Integer projectId);

    public List<ProjectApplicationVO> getMyProjectApplications(Integer applicantId);
}