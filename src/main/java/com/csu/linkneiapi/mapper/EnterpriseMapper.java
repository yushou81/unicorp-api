package com.csu.linkneiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.linkneiapi.entity.Enterprise;
import org.apache.ibatis.annotations.Mapper;

/**
 * 企业信息数据访问接口
 */
@Mapper
public interface EnterpriseMapper extends BaseMapper<Enterprise> {
} 