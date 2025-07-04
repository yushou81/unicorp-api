package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.ProjectFundRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 经费使用记录Mapper
 */
@Mapper
public interface ProjectFundRecordMapper extends BaseMapper<ProjectFundRecord> {}
