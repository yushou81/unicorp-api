package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.ProjectMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目成员Mapper接口
 */
@Mapper
public interface ProjectMemberMapper extends BaseMapper<ProjectMember> {
} 