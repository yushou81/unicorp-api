package com.csu.unicorp.common.constants;

/**
 * 日志操作类型枚举
 * 用于标识不同类型的系统操作
 */
public enum LogActionType {
    // 用户操作
    LOGIN("用户登录"),
    LOGOUT("用户登出"),
    REGISTER("用户注册"),
    UPDATE_PROFILE("更新个人信息"),
    CHANGE_PASSWORD("修改密码"),
    
    // 项目操作
    PROJECT_CREATE("创建项目"),
    PROJECT_UPDATE("更新项目"),
    PROJECT_DELETE("删除项目"),
    PROJECT_APPLY("申请项目"),
    PROJECT_APPROVE("批准项目申请"),
    PROJECT_REJECT("拒绝项目申请"),
    
    // 资源操作
    RESOURCE_UPLOAD("上传资源"),
    RESOURCE_DOWNLOAD("下载资源"),
    RESOURCE_DELETE("删除资源"),
    
    // 系统操作
    SYSTEM_ERROR("系统错误"),
    SYSTEM_WARNING("系统警告"),
    SYSTEM_INFO("系统信息");
    
    private final String description;
    
    LogActionType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 