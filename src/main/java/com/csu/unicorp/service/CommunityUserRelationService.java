package com.csu.unicorp.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.csu.unicorp.entity.community.CommunityUserRelation;
import com.csu.unicorp.vo.UserVO;

/**
 * 社区用户关系Service接口
 */
public interface CommunityUserRelationService extends IService<CommunityUserRelation> {
    
    /**
     * 关注用户
     * @param userId 用户ID
     * @param targetId 目标用户ID
     * @return 是否成功
     */
    boolean followUser(Long userId, Long targetId);
    
    /**
     * 取消关注用户
     * @param userId 用户ID
     * @param targetId 目标用户ID
     * @return 是否成功
     */
    boolean unfollowUser(Long userId, Long targetId);
    
    /**
     * 拉黑用户
     * @param userId 用户ID
     * @param targetId 目标用户ID
     * @return 是否成功
     */
    boolean blockUser(Long userId, Long targetId);
    
    /**
     * 取消拉黑用户
     * @param userId 用户ID
     * @param targetId 目标用户ID
     * @return 是否成功
     */
    boolean unblockUser(Long userId, Long targetId);
    
    /**
     * 检查用户是否已关注目标用户
     * @param userId 用户ID
     * @param targetId 目标用户ID
     * @return 是否已关注
     */
    boolean checkUserFollowed(Long userId, Long targetId);
    
    /**
     * 检查用户是否已拉黑目标用户
     * @param userId 用户ID
     * @param targetId 目标用户ID
     * @return 是否已拉黑
     */
    boolean checkUserBlocked(Long userId, Long targetId);
    
    /**
     * 获取用户关注列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 关注用户列表
     */
    Page<UserVO> getFollowings(Long userId, int page, int size);
    
    /**
     * 获取用户粉丝列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 粉丝用户列表
     */
    Page<UserVO> getFollowers(Long userId, int page, int size);
    
    /**
     * 获取用户拉黑列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 拉黑用户列表
     */
    Page<UserVO> getBlockedUsers(Long userId, int page, int size);
    
    /**
     * 获取用户关注的用户ID列表
     * @param userId 用户ID
     * @return 关注的用户ID列表
     */
    List<Long> getFollowingIds(Long userId);
    
    /**
     * 获取用户粉丝的用户ID列表
     * @param userId 用户ID
     * @return 粉丝的用户ID列表
     */
    List<Long> getFollowerIds(Long userId);
    
    /**
     * 获取用户拉黑的用户ID列表
     * @param userId 用户ID
     * @return 拉黑的用户ID列表
     */
    List<Long> getBlockedIds(Long userId);
    
    /**
     * 获取用户关注数量
     * @param userId 用户ID
     * @return 关注数量
     */
    int countFollowings(Long userId);
    
    /**
     * 获取用户粉丝数量
     * @param userId 用户ID
     * @return 粉丝数量
     */
    int countFollowers(Long userId);
    
    /**
     * 批量检查用户是否已关注目标用户
     * @param userId 用户ID
     * @param targetIds 目标用户ID列表
     * @return 已关注的用户ID列表
     */
    List<Long> batchCheckUserFollowed(Long userId, List<Long> targetIds);
} 