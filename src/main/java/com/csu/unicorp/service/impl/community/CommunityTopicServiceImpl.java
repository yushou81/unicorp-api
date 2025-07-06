package com.csu.unicorp.service.impl.community;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.constants.CacheConstants;
import com.csu.unicorp.dto.community.TopicDTO;
import com.csu.unicorp.entity.community.CommunityContentTag;
import com.csu.unicorp.entity.community.CommunityTopic;
import com.csu.unicorp.mapper.community.CommunityContentTagMapper;
import com.csu.unicorp.mapper.community.CommunityTopicMapper;
import com.csu.unicorp.service.CacheService;
import com.csu.unicorp.service.CommunityCategoryService;
import com.csu.unicorp.service.CommunityFavoriteService;
import com.csu.unicorp.service.CommunityLikeService;
import com.csu.unicorp.service.CommunityTagService;
import com.csu.unicorp.service.CommunityTopicService;
import com.csu.unicorp.vo.community.CategoryVO;
import com.csu.unicorp.vo.community.TagVO;
import com.csu.unicorp.vo.community.TopicVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 社区话题Service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityTopicServiceImpl extends ServiceImpl<CommunityTopicMapper, CommunityTopic> implements CommunityTopicService {

    private final CommunityTopicMapper topicMapper;
    private final CommunityContentTagMapper contentTagMapper;
    private final CommunityTagService tagService;
    private final CommunityCategoryService categoryService;
    private final CommunityLikeService likeService;
    private final CommunityFavoriteService favoriteService;
    private final CacheService cacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTopic(Long userId, TopicDTO topicDTO) {
        // 创建话题
        CommunityTopic topic = new CommunityTopic();
        topic.setTitle(topicDTO.getTitle());
        topic.setContent(topicDTO.getContent());
        topic.setUserId(userId);
        topic.setCategoryId(topicDTO.getCategoryId());
        topic.setViewCount(0);
        topic.setCommentCount(0);
        topic.setLikeCount(0);
        topic.setIsSticky(false);
        topic.setIsEssence(false);
        topic.setStatus("NORMAL");
        topic.setCreatedAt(LocalDateTime.now());
        topic.setUpdatedAt(LocalDateTime.now());
        
        topicMapper.insert(topic);
        Long topicId = topic.getId();
        
        // 保存标签关联
        saveTopicTags(topicId, topicDTO.getTagIds());
        
        // 清除话题列表缓存
        clearTopicListCache();
        
        return topicId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTopic(Long userId, Long topicId, TopicDTO topicDTO) {
        CommunityTopic topic = topicMapper.selectById(topicId);
        if (topic == null || "DELETED".equals(topic.getStatus())) {
            return false;
        }
        
        topic.setTitle(topicDTO.getTitle());
        topic.setContent(topicDTO.getContent());
        topic.setCategoryId(topicDTO.getCategoryId());
        topic.setUpdatedAt(LocalDateTime.now());
        
        topicMapper.updateById(topic);
        
        // 更新标签关联
        tagService.updateContentTags("TOPIC", topicId, topicDTO.getTagIds());
        
        // 清除话题缓存
        clearTopicCache(topicId);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTopic(Long userId, Long topicId) {
        CommunityTopic topic = topicMapper.selectById(topicId);
        if (topic == null || "DELETED".equals(topic.getStatus())) {
            return false;
        }
        
        topic.setStatus("DELETED");
        topic.setUpdatedAt(LocalDateTime.now());
        
        boolean result = topicMapper.updateById(topic) > 0;
        
        if (result) {
            // 清除话题缓存
            clearTopicCache(topicId);
            // 清除话题列表缓存
            clearTopicListCache();
        }
        
        return result;
    }

    @Override
    public TopicVO getTopicDetail(Long topicId, Long userId) {
        // 尝试从缓存获取话题详情
        String cacheKey = CacheConstants.TOPIC_DETAIL_CACHE_KEY_PREFIX + topicId;
        TopicVO cachedTopic = cacheService.get(cacheKey, TopicVO.class);
        
        if (cachedTopic != null) {
            // 如果缓存存在，设置用户交互状态
            if (userId != null) {
                cachedTopic.setLiked(likeService.checkLike(userId, topicId, "TOPIC"));
                cachedTopic.setFavorited(favoriteService.checkFavorite(userId, topicId, "TOPIC"));
            }
            
            // 增加浏览次数（异步操作，不影响缓存）
            incrementViewCount(topicId);
            
            return cachedTopic;
        }
        
        // 缓存不存在，从数据库查询
        CommunityTopic topic = topicMapper.selectById(topicId);
        if (topic == null || "DELETED".equals(topic.getStatus())) {
            return null;
        }
        
        TopicVO topicVO = convertToTopicVO(topic, userId);
        
        // 将话题详情缓存，有效期30分钟
        cacheService.set(cacheKey, topicVO, CacheConstants.TOPIC_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        // 增加浏览次数
        incrementViewCount(topicId);
        
        return topicVO;
    }

    @Override
    public Page<TopicVO> getTopicsByCategory(Long categoryId, int page, int size, Long userId) {
        Page<CommunityTopic> topicPage = new Page<>(page, size);
        Page<CommunityTopic> resultPage = topicMapper.selectTopicsByCategoryId(topicPage, categoryId);
        
        return convertToTopicVOPage(resultPage, userId);
    }

    @Override
    public Page<TopicVO> getHotTopics(int page, int size, Long userId) {
        // 热门话题第一页缓存
        if (page == 1 && size <= 20) {
            String cacheKey = CacheConstants.HOT_TOPICS_CACHE_KEY + ":" + size;
            List<TopicVO> cachedTopics = cacheService.getList(cacheKey, TopicVO.class);
            
            if (!CollectionUtils.isEmpty(cachedTopics)) {
                // 设置用户交互状态
                if (userId != null) {
                    setUserInteractionStatus(cachedTopics, userId);
                }
                
                Page<TopicVO> cachedPage = new Page<>(page, size, cachedTopics.size());
                cachedPage.setRecords(cachedTopics);
                return cachedPage;
            }
            
            // 缓存不存在，查询数据库
            Page<CommunityTopic> topicPage = new Page<>(page, size);
            Page<CommunityTopic> resultPage = topicMapper.selectHotTopics(topicPage);
            
            Page<TopicVO> voPage = convertToTopicVOPage(resultPage, userId);
            
            // 缓存结果，有效期1小时
            cacheService.setList(cacheKey, voPage.getRecords(), CacheConstants.HOT_TOPICS_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
            
            return voPage;
        }
        
        // 非第一页直接查询数据库
        Page<CommunityTopic> topicPage = new Page<>(page, size);
        Page<CommunityTopic> resultPage = topicMapper.selectHotTopics(topicPage);
        
        return convertToTopicVOPage(resultPage, userId);
    }

    @Override
    public Page<TopicVO> getLatestTopics(int page, int size, Long userId) {
        // 最新话题第一页缓存
        if (page == 1 && size <= 20) {
            String cacheKey = CacheConstants.LATEST_TOPICS_CACHE_KEY + ":" + size;
            List<TopicVO> cachedTopics = cacheService.getList(cacheKey, TopicVO.class);
            
            if (!CollectionUtils.isEmpty(cachedTopics)) {
                // 设置用户交互状态
                if (userId != null) {
                    setUserInteractionStatus(cachedTopics, userId);
                }
                
                Page<TopicVO> cachedPage = new Page<>(page, size, cachedTopics.size());
                cachedPage.setRecords(cachedTopics);
                return cachedPage;
            }
            
            // 缓存不存在，查询数据库
            Page<CommunityTopic> topicPage = new Page<>(page, size);
            Page<CommunityTopic> resultPage = topicMapper.selectLatestTopics(topicPage);
            
            Page<TopicVO> voPage = convertToTopicVOPage(resultPage, userId);
            
            // 缓存结果，最新话题缓存时间较短
            cacheService.setList(cacheKey, voPage.getRecords(), CacheConstants.TOPIC_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
            
            return voPage;
        }
        
        // 非第一页直接查询数据库
        Page<CommunityTopic> topicPage = new Page<>(page, size);
        Page<CommunityTopic> resultPage = topicMapper.selectLatestTopics(topicPage);
        
        return convertToTopicVOPage(resultPage, userId);
    }

    @Override
    public Page<TopicVO> getEssenceTopics(int page, int size, Long userId) {
        // 精华话题第一页缓存
        if (page == 1 && size <= 20) {
            String cacheKey = CacheConstants.FEATURED_TOPICS_CACHE_KEY + ":" + size;
            List<TopicVO> cachedTopics = cacheService.getList(cacheKey, TopicVO.class);
            
            if (!CollectionUtils.isEmpty(cachedTopics)) {
                // 设置用户交互状态
                if (userId != null) {
                    setUserInteractionStatus(cachedTopics, userId);
                }
                
                Page<TopicVO> cachedPage = new Page<>(page, size, cachedTopics.size());
                cachedPage.setRecords(cachedTopics);
                return cachedPage;
            }
            
            // 缓存不存在，查询数据库
            Page<CommunityTopic> topicPage = new Page<>(page, size);
            Page<CommunityTopic> resultPage = topicMapper.selectEssenceTopics(topicPage);
            
            Page<TopicVO> voPage = convertToTopicVOPage(resultPage, userId);
            
            // 缓存结果
            cacheService.setList(cacheKey, voPage.getRecords(), CacheConstants.TOPIC_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
            
            return voPage;
        }
        
        // 非第一页直接查询数据库
        Page<CommunityTopic> topicPage = new Page<>(page, size);
        Page<CommunityTopic> resultPage = topicMapper.selectEssenceTopics(topicPage);
        
        return convertToTopicVOPage(resultPage, userId);
    }

    @Override
    public Page<TopicVO> getUserTopics(Long userId, int page, int size, Long currentUserId) {
        Page<CommunityTopic> topicPage = new Page<>(page, size);
        Page<CommunityTopic> resultPage = topicMapper.selectTopicsByUserId(topicPage, userId);
        
        return convertToTopicVOPage(resultPage, currentUserId);
    }

    @Override
    public boolean setTopicSticky(Long topicId, boolean isSticky) {
        CommunityTopic topic = topicMapper.selectById(topicId);
        if (topic == null || "DELETED".equals(topic.getStatus())) {
            return false;
        }
        
        boolean result = topicMapper.updateStickyStatus(topicId, isSticky) > 0;
        
        if (result) {
            // 清除话题缓存
            clearTopicCache(topicId);
            // 清除话题列表缓存
            clearTopicListCache();
        }
        
        return result;
    }

    @Override
    public boolean setTopicEssence(Long topicId, boolean isEssence) {
        CommunityTopic topic = topicMapper.selectById(topicId);
        if (topic == null || "DELETED".equals(topic.getStatus())) {
            return false;
        }
        
        boolean result = topicMapper.updateEssenceStatus(topicId, isEssence) > 0;
        
        if (result) {
            // 清除话题缓存
            clearTopicCache(topicId);
            // 清除话题列表缓存
            clearTopicListCache();
        }
        
        return result;
    }

    @Override
    public void incrementViewCount(Long topicId) {
        topicMapper.incrementViewCount(topicId);
    }

    @Override
    public Page<TopicVO> searchTopics(String keyword, int page, int size, Long userId) {
        Page<CommunityTopic> topicPage = new Page<>(page, size);
        Page<CommunityTopic> resultPage = topicMapper.searchTopics(topicPage, keyword);
        
        return convertToTopicVOPage(resultPage, userId);
    }

    @Override
    public List<TopicVO> getRecommendTopics(Long userId, int limit) {
        // 简单实现，获取最热门的话题
        Page<CommunityTopic> topicPage = new Page<>(1, limit);
        Page<CommunityTopic> resultPage = topicMapper.selectHotTopics(topicPage);
        
        List<TopicVO> topicVOList = new ArrayList<>();
        for (CommunityTopic topic : resultPage.getRecords()) {
            topicVOList.add(convertToTopicVO(topic, userId));
        }
        
        return topicVOList;
    }

    @Override
    public boolean checkTopicEditPermission(Long userId, Long topicId) {
        CommunityTopic topic = topicMapper.selectById(topicId);
        if (topic == null) {
            return false;
        }
        
        // 话题作者或管理员可以编辑
        return topic.getUserId().equals(userId) || isAdmin(userId);
    }
    
    /**
     * 保存话题标签关联
     * @param topicId 话题ID
     * @param tagIds 标签ID列表
     */
    private void saveTopicTags(Long topicId, List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }
        
        // 使用tagService的addTagsToContent方法添加标签关联，同时更新标签使用次数
        tagService.addTagsToContent("TOPIC", topicId, tagIds);
    }
    
    /**
     * 转换话题实体为话题VO
     * @param topic 话题实体
     * @param userId 当前用户ID
     * @return 话题VO
     */
    private TopicVO convertToTopicVO(CommunityTopic topic, Long userId) {
        TopicVO topicVO = new TopicVO();
        topicVO.setId(topic.getId());
        topicVO.setTitle(topic.getTitle());
        topicVO.setContent(topic.getContent());
        topicVO.setUserId(topic.getUserId());
        topicVO.setCategoryId(topic.getCategoryId());
        topicVO.setViewCount(topic.getViewCount());
        topicVO.setCommentCount(topic.getCommentCount());
        topicVO.setLikeCount(topic.getLikeCount());
        topicVO.setIsSticky(topic.getIsSticky());
        topicVO.setIsEssence(topic.getIsEssence());
        topicVO.setStatus(topic.getStatus());
        topicVO.setCreatedAt(topic.getCreatedAt());
        topicVO.setUpdatedAt(topic.getUpdatedAt());
        
        // 获取板块信息
        CategoryVO categoryVO = categoryService.getCategoryDetail(topic.getCategoryId());
        if (categoryVO != null) {
            topicVO.setCategoryName(categoryVO.getName());
        }
        
        // TODO: 获取用户信息，需要用户服务
        topicVO.setUserName("用户" + topic.getUserId());
        topicVO.setUserAvatar("/avatars/default/avatar.jpg");
        
        // 获取标签列表
        List<TagVO> tagList = tagService.getTopicTags(topic.getId());
        topicVO.setTags(tagList != null ? tagList : Collections.emptyList());
        
        // 设置当前用户是否已点赞、收藏
        if (userId != null) {
            topicVO.setLiked(likeService.checkLike(userId, topic.getId(), "TOPIC"));
            topicVO.setFavorited(favoriteService.checkFavorite(userId, topic.getId(), "TOPIC"));
        } else {
            topicVO.setLiked(false);
            topicVO.setFavorited(false);
        }
        
        return topicVO;
    }
    
    /**
     * 转换话题分页为话题VO分页
     * @param topicPage 话题分页
     * @param userId 当前用户ID
     * @return 话题VO分页
     */
    private Page<TopicVO> convertToTopicVOPage(Page<CommunityTopic> topicPage, Long userId) {
        Page<TopicVO> topicVOPage = new Page<>(topicPage.getCurrent(), topicPage.getSize(), topicPage.getTotal());
        
        List<TopicVO> topicVOList = topicPage.getRecords().stream()
                .map(topic -> convertToTopicVO(topic, userId))
                .collect(Collectors.toList());
        
        topicVOPage.setRecords(topicVOList);
        return topicVOPage;
    }

    /**
     * 设置用户交互状态（点赞、收藏）
     * @param topicVOList 话题VO列表
     * @param userId 当前用户ID
     */
    private void setUserInteractionStatus(List<TopicVO> topicVOList, Long userId) {
        if (CollectionUtils.isEmpty(topicVOList) || userId == null) {
            return;
        }
        
        // 获取话题ID列表
        List<Long> topicIds = topicVOList.stream()
                .map(TopicVO::getId)
                .collect(Collectors.toList());
        
        // 批量查询用户点赞状态
        List<Long> likedTopicIds = likeService.batchCheckUserLiked(userId, "TOPIC", topicIds);
        
        // 批量查询用户收藏状态
        List<Long> favoritedTopicIds = favoriteService.batchCheckUserFavorited(userId, "TOPIC", topicIds);
        
        // 设置交互状态
        for (TopicVO topic : topicVOList) {
            topic.setLiked(likedTopicIds.contains(topic.getId()));
            topic.setFavorited(favoritedTopicIds.contains(topic.getId()));
        }
    }
    
    /**
     * 判断用户是否是管理员
     * @param userId 用户ID
     * @return 是否是管理员
     */
    private boolean isAdmin(Long userId) {
        // TODO: 实际项目中应该通过权限系统判断是否是管理员
        return false;
    }
    
    /**
     * 清除话题缓存
     * @param topicId 话题ID
     */
    private void clearTopicCache(Long topicId) {
        if (topicId == null) {
            return;
        }
        
        String cacheKey = CacheConstants.TOPIC_DETAIL_CACHE_KEY_PREFIX + topicId;
        cacheService.delete(cacheKey);
        log.debug("清除话题缓存: {}", cacheKey);
    }
    
    /**
     * 清除话题列表缓存
     */
    private void clearTopicListCache() {
        // 清除热门话题缓存
        cacheService.deleteByPattern(CacheConstants.HOT_TOPICS_CACHE_KEY + "*");
        // 清除最新话题缓存
        cacheService.deleteByPattern(CacheConstants.LATEST_TOPICS_CACHE_KEY + "*");
        // 清除精华话题缓存
        cacheService.deleteByPattern(CacheConstants.FEATURED_TOPICS_CACHE_KEY + "*");
        log.debug("清除话题列表缓存");
    }
} 