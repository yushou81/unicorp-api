package com.csu.unicorp.dto;

import java.util.List;

public class ProjectPermissionRevokeDTO {
    private Integer projectId;
    private Integer userId;
    private List<String> roles;

    public Integer getProjectId() { return projectId; }
    public void setProjectId(Integer projectId) { this.projectId = projectId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
} 