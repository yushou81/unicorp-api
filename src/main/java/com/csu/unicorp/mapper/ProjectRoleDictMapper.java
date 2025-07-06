package com.csu.unicorp.mapper;

import com.csu.unicorp.entity.ProjectRoleDict;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface ProjectRoleDictMapper {
    List<ProjectRoleDict> selectAll();
} 