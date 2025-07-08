package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.ProjectLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目操作日志Mapper
 */
@Mapper
public interface ProjectLogMapper extends BaseMapper<ProjectLog> {}
