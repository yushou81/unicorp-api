// src/main/java/com/csu/unicorp/mapper/ProjectContractMapper.java
package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.ProjectContract;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectContractMapper extends BaseMapper<ProjectContract> {
    // BaseMapper 已包含常用CRUD方法
}