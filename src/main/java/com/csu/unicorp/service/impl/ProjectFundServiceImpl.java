package com.csu.unicorp.service.impl;

import com.csu.unicorp.dto.ProjectFundApplyDTO;
import com.csu.unicorp.dto.ProjectFundReviewDTO;
import com.csu.unicorp.entity.ProjectFund;
import com.csu.unicorp.entity.ProjectFundRecord;
import com.csu.unicorp.mapper.ProjectFundMapper;
import com.csu.unicorp.mapper.ProjectFundRecordMapper;
import com.csu.unicorp.service.ProjectFundService;
import com.csu.unicorp.vo.ProjectFundVO;
import com.csu.unicorp.vo.ProjectFundRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectFundServiceImpl implements ProjectFundService {
    private final ProjectFundMapper fundMapper;
    private final ProjectFundRecordMapper fundRecordMapper;
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
}
