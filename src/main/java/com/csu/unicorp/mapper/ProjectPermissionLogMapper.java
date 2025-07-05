package com.csu.unicorp.mapper;

import com.csu.unicorp.entity.ProjectPermissionLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProjectPermissionLogMapper {
    int insert(ProjectPermissionLog log);
    List<ProjectPermissionLog> selectByProjectId(@Param("projectId") Integer projectId);
} 