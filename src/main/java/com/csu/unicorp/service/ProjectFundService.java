package com.csu.unicorp.service;

import com.csu.unicorp.dto.ProjectFundApplyDTO;
import com.csu.unicorp.dto.ProjectFundReviewDTO;
import com.csu.unicorp.vo.ProjectFundVO;
import com.csu.unicorp.vo.ProjectFundRecordVO;
import java.util.List;

/**
 * 项目经费服务接口
 */
public interface ProjectFundService {
    ProjectFundVO applyFund(Integer projectId, ProjectFundApplyDTO dto);
    ProjectFundVO reviewFund(Integer projectId, Integer fundId, ProjectFundReviewDTO dto);
    List<ProjectFundRecordVO> getFundRecords(Integer projectId);
}
