package com.csu.unicorp.dto;

import java.util.Date;
import java.util.List;

public class ProjectPermissionAssignDTO {
    private Integer projectId;
    private Integer userId;
    private List<String> roles;
    private Date expireAt;

    public Integer getProjectId() { return projectId; }
    public void setProjectId(Integer projectId) { this.projectId = projectId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public Date getExpireAt() { return expireAt; }
    public void setExpireAt(Date expireAt) { this.expireAt = expireAt; }
} 