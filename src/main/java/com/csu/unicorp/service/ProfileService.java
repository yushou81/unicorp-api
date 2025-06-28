package com.csu.unicorp.service;

/**
 * 用户个人资料服务接口
 */
public interface ProfileService {
    
    /**
     * 更新用户头像
     * 
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     */
    void updateAvatar(Integer userId, String avatarUrl);
    
    /**
     * 更新学生简历
     * 
     * @param userId 用户ID
     * @param resumeUrl 简历URL
     */
    void updateResume(Integer userId, String resumeUrl);
} 