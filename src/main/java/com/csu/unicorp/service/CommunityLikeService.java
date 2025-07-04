package com.csu.unicorp.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.csu.unicorp.entity.community.CommunityLike;
import com.csu.unicorp.vo.community.AnswerVO;

/**
 * 社区点赞Service接口
 */
public interface CommunityLikeService extends IService<CommunityLike> {
    
    /**
     * 点赞内容
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 是否成功
     */
    boolean likeContent(Long userId, String contentType, Long contentId);
    
    /**
     * 取消点赞内容
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 是否成功
     */
    boolean unlikeContent(Long userId, String contentType, Long contentId);
    
    /**
     * 检查用户是否已点赞内容
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 是否已点赞
     */
    boolean checkUserLiked(Long userId, String contentType, Long contentId);
    
    /**
     * 获取用户点赞的内容ID列表
     * @param userId 用户ID
     * @param contentType 内容类型
     * @return 内容ID列表
     */
    List<Long> getUserLikedContentIds(Long userId, String contentType);
    
    /**
     * 获取用户点赞列表（分页）
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param page 页码
     * @param size 每页大小
     * @return 点赞列表
     */
    Page<CommunityLike> getUserLikes(Long userId, String contentType, int page, int size);
    
    /**
     * 获取内容的点赞数量
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 点赞数量
     */
    int getContentLikeCount(String contentType, Long contentId);
    
    /**
     * 批量检查用户是否已点赞内容
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentIds 内容ID列表
     * @return 已点赞的内容ID列表
     */
    List<Long> batchCheckUserLiked(Long userId, String contentType, List<Long> contentIds);
    
    /**
     * 点赞
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 是否成功
     */
    boolean like(Long userId, Long targetId, String targetType);
    
    /**
     * 取消点赞
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     */
    void unlike(Long userId, Long targetId, String targetType);
    
    /**
     * 检查用户是否点赞
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 是否已点赞
     */
    boolean checkLike(Long userId, Long targetId, String targetType);
    
    /**
     * 获取用户点赞的回答列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 回答列表
     */
    Page<AnswerVO> getLikedAnswers(Long userId, int page, int size);
} 