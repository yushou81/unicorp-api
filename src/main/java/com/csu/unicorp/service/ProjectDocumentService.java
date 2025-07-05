package com.csu.unicorp.service;

import com.csu.unicorp.dto.ProjectDocumentDTO;
import com.csu.unicorp.vo.ProjectDocumentVO;
import java.util.List;

/**
 * 项目资料/合同服务接口
 */
public interface ProjectDocumentService {
    ProjectDocumentVO addDocument(Integer projectId, ProjectDocumentDTO dto);
    List<ProjectDocumentVO> getDocuments(Integer projectId);
}
