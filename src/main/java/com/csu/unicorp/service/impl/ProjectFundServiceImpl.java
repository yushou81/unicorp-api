package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.csu.unicorp.dto.ProjectFundApplyDTO;
import com.csu.unicorp.dto.ProjectFundReviewDTO;
import com.csu.unicorp.entity.FileMapping;
import com.csu.unicorp.entity.ProjectFund;
import com.csu.unicorp.entity.ProjectFundRecord;
import com.csu.unicorp.mapper.FileMappingMapper;
import com.csu.unicorp.mapper.ProjectFundMapper;
import com.csu.unicorp.mapper.ProjectFundRecordMapper;
import com.csu.unicorp.service.ProjectFundService;
import com.csu.unicorp.vo.ProjectFundVO;
import com.csu.unicorp.vo.ProjectFundRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectFundServiceImpl implements ProjectFundService {
    @Autowired
    private final ProjectFundMapper fundMapper;
    @Autowired
    private final ProjectFundRecordMapper fundRecordMapper;
    @Autowired
    private FileMappingMapper fileMappingMapper;
    public ProjectFundServiceImpl(ProjectFundMapper fundMapper, ProjectFundRecordMapper fundRecordMapper) {
        this.fundMapper = fundMapper;
        this.fundRecordMapper = fundRecordMapper;
    }

    @Override
    public ProjectFundVO applyFund(Integer projectId, ProjectFundApplyDTO dto) {
        ProjectFund fund = new ProjectFund();
        BeanUtils.copyProperties(dto, fund);
        fund.setProjectId(projectId);
        if (dto.getAttachments() != null) {
            fund.setAttachments(String.join(",", dto.getAttachments()));
        }
        fund.setStatus("pending");
        fund.setCreateTime(new java.sql.Timestamp(System.currentTimeMillis())); // 设置申请时间
        fundMapper.insert(fund);
        ProjectFundVO vo = new ProjectFundVO();
        BeanUtils.copyProperties(fund, vo);
        vo.setAttachments(dto.getAttachments());
        return vo;
    }

    @Override
    public ProjectFundVO reviewFund(Integer projectId, Integer fundId, ProjectFundReviewDTO dto) {
        ProjectFund fund = fundMapper.selectById(fundId);
        fund.setStatus(dto.getStatus());
        if ("approved".equals(dto.getStatus())) {
            fund.setApprovedTime(new java.sql.Timestamp(System.currentTimeMillis())); // 设置同意时间
        } else if ("rejected".equals(dto.getStatus())) {
            fund.setRejectedTime(new java.sql.Timestamp(System.currentTimeMillis())); // 设置拒绝时间
        }
        fundMapper.updateById(fund);
        ProjectFundVO vo = new ProjectFundVO();
        BeanUtils.copyProperties(fund, vo);
        return vo;
    }

    @Override
    public List<ProjectFundRecordVO> getFundRecords(Integer projectId) {
        List<ProjectFundRecord> list = fundRecordMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ProjectFundRecord>().eq("project_id", projectId));
        return list.stream().map(record -> {
            ProjectFundRecordVO vo = new ProjectFundRecordVO();
            BeanUtils.copyProperties(record, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ProjectFundVO> getFundList(Integer projectId) {
        List<ProjectFund> list = fundMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ProjectFund>().eq("project_id", projectId));
        return list.stream().map(fund -> {
            ProjectFundVO vo = new ProjectFundVO();
            BeanUtils.copyProperties(fund, vo);
            if (fund.getAttachments() != null) {
                List<String> storedNames = List.of(fund.getAttachments().split(","));
                vo.setAttachments(storedNames);
                // 批量查找原始文件名
                if (!storedNames.isEmpty()) {
                    List<FileMapping> mappings = fileMappingMapper.selectList(
                        new QueryWrapper<FileMapping>().in("stored_name", storedNames)
                    );
                    List<String> originalNames = storedNames.stream()
                        .map(stored -> mappings.stream()
                            .filter(m -> m.getStoredName().equals(stored))
                            .map(FileMapping::getOriginalName)
                            .findFirst().orElse(stored))
                        .collect(Collectors.toList());
                    vo.setOriginalName(originalNames);
                } else {
                    vo.setOriginalName(List.of());
                }
            } else {
                vo.setAttachments(List.of());
                vo.setOriginalName(List.of());
            }
            return vo;
        }).collect(Collectors.toList());
    }
}
