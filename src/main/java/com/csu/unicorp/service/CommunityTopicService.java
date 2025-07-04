package com.csu.unicorp.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.csu.unicorp.dto.community.TopicDTO;
import com.csu.unicorp.entity.community.CommunityTopic;
import com.csu.unicorp.vo.community.TopicVO;

/**
 * 社区话题Service接口
 */
public interface CommunityTopicService extends IService<CommunityTopic> {
    
    /**
     * 创建话题
     * @param userId 用户ID
     * @param topicDTO 话题DTO
     * @return 话题ID
     */
    Long createTopic(Long userId, TopicDTO topicDTO);
    
    /**
     * 更新话题
     * @param userId 用户ID
     * @param topicId 话题ID
     * @param topicDTO 话题DTO
     * @return 是否成功
     */
    boolean updateTopic(Long userId, Long topicId, TopicDTO topicDTO);
    
    /**
     * 删除话题
     * @param userId 用户ID
     * @param topicId 话题ID
     * @return 是否成功
     */
    boolean deleteTopic(Long userId, Long topicId);
    
    /**
     * 获取话题详情
     * @param topicId 话题ID
     * @param userId 当前用户ID（可选）
     * @return 话题详情
     */
    TopicVO getTopicDetail(Long topicId, Long userId);
    
    /**
     * 获取板块话题列表
     * @param categoryId 板块ID
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID（可选）
     * @return 话题列表
     */
    Page<TopicVO> getTopicsByCategory(Long categoryId, int page, int size, Long userId);
    
    /**
     * 获取热门话题列表
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID（可选）
     * @return 热门话题列表
     */
    Page<TopicVO> getHotTopics(int page, int size, Long userId);
    
    /**
     * 获取最新话题列表
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID（可选）
     * @return 最新话题列表
     */
    Page<TopicVO> getLatestTopics(int page, int size, Long userId);
    
    /**
     * 获取精华话题列表
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID（可选）
     * @return 精华话题列表
     */
    Page<TopicVO> getEssenceTopics(int page, int size, Long userId);
    
    /**
     * 获取用户话题列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param currentUserId 当前用户ID（可选）
     * @return 用户话题列表
     */
    Page<TopicVO> getUserTopics(Long userId, int page, int size, Long currentUserId);
    
    /**
     * 设置话题置顶状态
     * @param topicId 话题ID
     * @param isSticky 是否置顶
     * @return 是否成功
     */
    boolean setTopicSticky(Long topicId, boolean isSticky);
    
    /**
     * 设置话题精华状态
     * @param topicId 话题ID
     * @param isEssence 是否精华
     * @return 是否成功
     */
    boolean setTopicEssence(Long topicId, boolean isEssence);
    
    /**
     * 增加话题浏览次数
     * @param topicId 话题ID
     */
    void incrementViewCount(Long topicId);
    
    /**
     * 搜索话题
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID（可选）
     * @return 话题列表
     */
    Page<TopicVO> searchTopics(String keyword, int page, int size, Long userId);
    
    /**
     * 获取推荐话题列表
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 推荐话题列表
     */
    List<TopicVO> getRecommendTopics(Long userId, int limit);
    
    /**
     * 检查用户是否有权限编辑话题
     * @param userId 用户ID
     * @param topicId 话题ID
     * @return 是否有权限
     */
    boolean checkTopicEditPermission(Long userId, Long topicId);
} 