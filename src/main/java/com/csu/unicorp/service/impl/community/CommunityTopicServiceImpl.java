package com.csu.unicorp.service.impl.community;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.dto.community.TopicDTO;
import com.csu.unicorp.entity.community.CommunityContentTag;
import com.csu.unicorp.entity.community.CommunityTopic;
import com.csu.unicorp.mapper.community.CommunityContentTagMapper;
import com.csu.unicorp.mapper.community.CommunityTopicMapper;
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
        
        return topicMapper.updateById(topic) > 0;
    }

    @Override
    public TopicVO getTopicDetail(Long topicId, Long userId) {
        CommunityTopic topic = topicMapper.selectById(topicId);
        if (topic == null || "DELETED".equals(topic.getStatus())) {
            return null;
        }
        
        return convertToTopicVO(topic, userId);
    }

    @Override
    public Page<TopicVO> getTopicsByCategory(Long categoryId, int page, int size, Long userId) {
        Page<CommunityTopic> topicPage = new Page<>(page, size);
        Page<CommunityTopic> resultPage = topicMapper.selectTopicsByCategoryId(topicPage, categoryId);
        
        return convertToTopicVOPage(resultPage, userId);
    }

    @Override
    public Page<TopicVO> getHotTopics(int page, int size, Long userId) {
        Page<CommunityTopic> topicPage = new Page<>(page, size);
        Page<CommunityTopic> resultPage = topicMapper.selectHotTopics(topicPage);
        
        return convertToTopicVOPage(resultPage, userId);
    }

    @Override
    public Page<TopicVO> getLatestTopics(int page, int size, Long userId) {
        Page<CommunityTopic> topicPage = new Page<>(page, size);
        Page<CommunityTopic> resultPage = topicMapper.selectLatestTopics(topicPage);
        
        return convertToTopicVOPage(resultPage, userId);
    }

    @Override
    public Page<TopicVO> getEssenceTopics(int page, int size, Long userId) {
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
        
        return topicMapper.updateStickyStatus(topicId, isSticky) > 0;
    }

    @Override
    public boolean setTopicEssence(Long topicId, boolean isEssence) {
        CommunityTopic topic = topicMapper.selectById(topicId);
        if (topic == null || "DELETED".equals(topic.getStatus())) {
            return false;
        }
        
        return topicMapper.updateEssenceStatus(topicId, isEssence) > 0;
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
        // TODO: 实际项目中应该通过权限系统判断是否是管理员
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
            topicVO.setLiked(likeService.checkLike(userId, topic.getId(), "topic"));
            topicVO.setFavorited(favoriteService.checkFavorite(userId, topic.getId(), "topic"));
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
     * 判断用户是否是管理员
     * @param userId 用户ID
     * @return 是否是管理员
     */
    private boolean isAdmin(Long userId) {
        // TODO: 实际项目中应该通过权限系统判断是否是管理员
        return false;
    }
} 