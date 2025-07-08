package com.csu.unicorp.service;

import com.csu.unicorp.dto.ProjectContractCreationDTO;
import com.csu.unicorp.dto.ProjectContractStatusUpdateDTO;
import com.csu.unicorp.vo.ProjectContractVO;

import java.util.List;

public interface ProjectContractService {
    ProjectContractVO createContract(Integer projectId, ProjectContractCreationDTO dto, Integer initiatorId);
    ProjectContractVO getContractById(Integer projectId, Integer contractId);
    ProjectContractVO updateContractStatus(Integer projectId, Integer contractId, ProjectContractStatusUpdateDTO dto);
    List<ProjectContractVO> getContractsByProjectId(Integer projectId);
}