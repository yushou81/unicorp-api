// src/main/java/com/csu/unicorp/service/impl/ProjectContractServiceImpl.java
package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.csu.unicorp.dto.ProjectContractCreationDTO;
import com.csu.unicorp.dto.ProjectContractStatusUpdateDTO;
import com.csu.unicorp.entity.FileMapping;
import com.csu.unicorp.entity.ProjectContract;
import com.csu.unicorp.mapper.FileMappingMapper;
import com.csu.unicorp.mapper.ProjectContractMapper;
import com.csu.unicorp.service.ProjectContractService;
import com.csu.unicorp.vo.ProjectContractVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectContractServiceImpl implements ProjectContractService {

    private final ProjectContractMapper contractMapper;
    @Autowired
private FileMappingMapper fileMappingMapper;

    public ProjectContractServiceImpl(ProjectContractMapper contractMapper) {
        this.contractMapper = contractMapper;
    }

    @Override
    public ProjectContractVO createContract(Integer projectId, ProjectContractCreationDTO dto, Integer initiatorId) {
        ProjectContract contract = new ProjectContract();
        contract.setProjectId(projectId);
        contract.setContractName(dto.getContractName());
        contract.setContractUrl(dto.getContractUrl());
        contract.setStatus("pending");
        contract.setInitiatorId(initiatorId);
        contract.setReceiverId(dto.getReceiverId());
        contract.setRemark(dto.getRemark());
        contractMapper.insert(contract);

        ProjectContractVO vo = new ProjectContractVO();
        BeanUtils.copyProperties(contract, vo);
        return vo;
    }

    @Override
    public ProjectContractVO getContractById(Integer projectId, Integer contractId) {
        ProjectContract contract = contractMapper.selectById(contractId);
        if (contract == null || !contract.getProjectId().equals(projectId)) {
            return null;
        }
        ProjectContractVO vo = new ProjectContractVO();
        BeanUtils.copyProperties(contract, vo);
        return vo;
    }

    @Override
    public ProjectContractVO updateContractStatus(Integer projectId, Integer contractId, ProjectContractStatusUpdateDTO dto) {
        ProjectContract contract = contractMapper.selectById(contractId);
        if (contract == null || !contract.getProjectId().equals(projectId)) {
            return null;
        }
        contract.setStatus(dto.getStatus());
        if ("active".equals(dto.getStatus())) {
            contract.setSignTime(new Timestamp(System.currentTimeMillis()));
        }
        contract.setRemark(dto.getRemark());
        contractMapper.updateById(contract);

        ProjectContractVO vo = new ProjectContractVO();
        BeanUtils.copyProperties(contract, vo);
        return vo;
    }

    @Override
public List<ProjectContractVO> getContractsByProjectId(Integer projectId) {
    List<ProjectContract> list = contractMapper.selectList(
            new QueryWrapper<ProjectContract>().eq("project_id", projectId));
    return list.stream().map(contract -> {
        ProjectContractVO vo = new ProjectContractVO();
        BeanUtils.copyProperties(contract, vo);

        // 查找原始文件名
        if (contract.getContractUrl() != null) {
            FileMapping mapping = fileMappingMapper.selectOne(
                new QueryWrapper<FileMapping>().eq("stored_name", contract.getContractUrl())
            );
            if (mapping != null) {
                vo.setOriginalName(mapping.getOriginalName());
            } else {
                vo.setOriginalName(contract.getContractUrl());
            }
        }
        return vo;
    }).collect(Collectors.toList());
}
}