package com.csu.unicorp.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.csu.unicorp.entity.community.CommunityFavorite;
import com.csu.unicorp.vo.community.QuestionVO;
import com.csu.unicorp.vo.community.TopicVO;

/**
 * 社区收藏Service接口
 */
public interface CommunityFavoriteService extends IService<CommunityFavorite> {
    
    /**
     * 收藏内容
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 是否成功
     */
    boolean favoriteContent(Long userId, String contentType, Long contentId);
    
    /**
     * 取消收藏内容
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 是否成功
     */
    boolean unfavoriteContent(Long userId, String contentType, Long contentId);
    
    /**
     * 检查用户是否已收藏内容
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 是否已收藏
     */
    boolean checkUserFavorited(Long userId, String contentType, Long contentId);
    
    /**
     * 获取用户收藏的内容ID列表
     * @param userId 用户ID
     * @param contentType 内容类型
     * @return 内容ID列表
     */
    List<Long> getUserFavoritedContentIds(Long userId, String contentType);
    
    /**
     * 获取用户收藏列表（分页）
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param page 页码
     * @param size 每页大小
     * @return 收藏列表
     */
    Page<CommunityFavorite> getUserFavorites(Long userId, String contentType, int page, int size);
    
    /**
     * 获取内容的收藏数量
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 收藏数量
     */
    int getContentFavoriteCount(String contentType, Long contentId);
    
    /**
     * 批量检查用户是否已收藏内容
     * @param userId 用户ID
     * @param contentType 内容类型
     * @param contentIds 内容ID列表
     * @return 已收藏的内容ID列表
     */
    List<Long> batchCheckUserFavorited(Long userId, String contentType, List<Long> contentIds);
    
    /**
     * 收藏
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 是否成功
     */
    boolean favorite(Long userId, Long targetId, String targetType);
    
    /**
     * 取消收藏
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     */
    void unfavorite(Long userId, Long targetId, String targetType);
    
    /**
     * 检查用户是否收藏
     * @param userId 用户ID
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @return 是否已收藏
     */
    boolean checkFavorite(Long userId, Long targetId, String targetType);
    
    /**
     * 获取用户收藏的话题列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 话题列表
     */
    Page<TopicVO> getFavoriteTopics(Long userId, int page, int size);
    
    /**
     * 获取用户收藏的问题列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 问题列表
     */
    Page<QuestionVO> getFavoriteQuestions(Long userId, int page, int size);
} 