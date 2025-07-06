package com.csu.unicorp.service.impl;

import com.csu.unicorp.dto.ProjectDocumentDTO;
import com.csu.unicorp.entity.ProjectDocument;
import com.csu.unicorp.mapper.ProjectDocumentMapper;
import com.csu.unicorp.service.ProjectDocumentService;
import com.csu.unicorp.vo.ProjectDocumentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectDocumentServiceImpl implements ProjectDocumentService {
    private final ProjectDocumentMapper documentMapper;
    public ProjectDocumentServiceImpl(ProjectDocumentMapper documentMapper) { this.documentMapper = documentMapper; }

    @Override
    public ProjectDocumentVO addDocument(Integer projectId, ProjectDocumentDTO dto) {
        ProjectDocument doc = new ProjectDocument();
        BeanUtils.copyProperties(dto, doc);
        doc.setProjectId(projectId);
        documentMapper.insert(doc);
        ProjectDocumentVO vo = new ProjectDocumentVO();
        BeanUtils.copyProperties(doc, vo);
        return vo;
    }

    @Override
    public List<ProjectDocumentVO> getDocuments(Integer projectId) {
        List<ProjectDocument> list = documentMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ProjectDocument>().eq("project_id", projectId));
        return list.stream().map(doc -> {
            ProjectDocumentVO vo = new ProjectDocumentVO();
            BeanUtils.copyProperties(doc, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
