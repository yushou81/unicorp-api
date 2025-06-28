package com.csu.unicorp.service;

import com.csu.unicorp.dto.ProfileUpdateDTO;
import com.csu.unicorp.vo.UserProfileVO;

/**
 * 个人主页服务接口
 */
public interface ProfileService {
    
    /**
     * 获取指定用户的公开主页信息
     * 
     * @param userId 用户ID
     * @return 用户主页信息
     */
    UserProfileVO getUserProfile(Integer userId);
    
    /**
     * 获取当前登录用户的完整档案信息
     * 
     * @param currentUserId 当前登录用户ID
     * @return 用户档案信息
     */
    UserProfileVO getCurrentUserProfile(Integer currentUserId);
    
    /**
     * 更新当前登录用户的基本档案
     * 
     * @param currentUserId 当前登录用户ID
     * @param profileUpdateDTO 更新信息
     * @return 更新后的用户档案信息
     */
    UserProfileVO updateUserProfile(Integer currentUserId, ProfileUpdateDTO profileUpdateDTO);
} 