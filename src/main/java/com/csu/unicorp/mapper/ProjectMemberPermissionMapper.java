package com.csu.unicorp.mapper;

import com.csu.unicorp.entity.ProjectMemberPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProjectMemberPermissionMapper {
    int insert(ProjectMemberPermission permission);
    int deleteByProjectIdAndUserIdAndRole(@Param("projectId") Integer projectId, @Param("userId") Integer userId, @Param("role") String role);
    List<ProjectMemberPermission> selectByProjectId(@Param("projectId") Integer projectId);
    List<ProjectMemberPermission> selectByProjectIdAndUserId(@Param("projectId") Integer projectId, @Param("userId") Integer userId);
} 