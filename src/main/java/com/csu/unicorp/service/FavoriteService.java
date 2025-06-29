package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.vo.JobVO;
import com.csu.unicorp.vo.ResultVO;

/**
 * 岗位收藏服务接口
 */
public interface FavoriteService {
    
    /**
     * 收藏岗位
     * 
     * @param userId 用户ID
     * @param jobId 岗位ID
     * @return 操作结果
     */
    ResultVO<Void> favoriteJob(Integer userId, Integer jobId);
    
    /**
     * 取消收藏岗位
     * 
     * @param userId 用户ID
     * @param jobId 岗位ID
     * @return 操作结果
     */
    ResultVO<Void> unfavoriteJob(Integer userId, Integer jobId);
    
    /**
     * 获取用户收藏的岗位列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 岗位列表分页结果
     */
    IPage<JobVO> getFavoriteJobs(Integer userId, int page, int size);
    
    /**
     * 检查用户是否已收藏某个岗位
     * 
     * @param userId 用户ID
     * @param jobId 岗位ID
     * @return 是否已收藏
     */
    boolean checkFavorite(Integer userId, Integer jobId);
} 