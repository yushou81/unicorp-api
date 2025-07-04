package com.csu.unicorp.service.impl.community;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.constants.CacheConstants;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.community.CommunityFavorite;
import com.csu.unicorp.entity.community.CommunityQuestion;
import com.csu.unicorp.entity.community.CommunityTopic;
import com.csu.unicorp.mapper.community.CommunityFavoriteMapper;
import com.csu.unicorp.mapper.community.CommunityQuestionMapper;
import com.csu.unicorp.mapper.community.CommunityTopicMapper;
import com.csu.unicorp.service.CacheService;
import com.csu.unicorp.service.CommunityFavoriteService;
import com.csu.unicorp.service.CommunityLikeService;
import com.csu.unicorp.service.CommunityNotificationService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.community.QuestionVO;
import com.csu.unicorp.vo.community.TopicVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 社区收藏Service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityFavoriteServiceImpl extends ServiceImpl<CommunityFavoriteMapper, CommunityFavorite> implements CommunityFavoriteService {
    
    private final CommunityFavoriteMapper favoriteMapper;
    private final CommunityTopicMapper topicMapper;
    private final CommunityQuestionMapper questionMapper;
    private final CommunityLikeService likeService;
    private final CommunityNotificationService notificationService;
    private final UserService userService;
    private final CacheService cacheService;
    
    @Override
    @Transactional
    public boolean favoriteContent(Long userId, String contentType, Long contentId) {
        // 检查是否已经收藏
        if (checkUserFavorited(userId, contentType, contentId)) {
            return true;
        }
        
        // 创建收藏记录
        CommunityFavorite favorite = CommunityFavorite.builder()
                .userId(userId)
                .contentType(contentType)
                .contentId(contentId)
                .createdAt(LocalDateTime.now())
                .build();
        
        boolean result = save(favorite);
        
        if (result) {
            // 清除收藏计数缓存
            String countCacheKey = CacheConstants.CONTENT_FAVORITE_COUNT_CACHE_KEY_PREFIX + contentType + ":" + contentId;
            cacheService.delete(countCacheKey);
            
            // 清除用户收藏状态缓存
            String statusCacheKey = CacheConstants.USER_FAVORITE_STATUS_CACHE_KEY_PREFIX + userId + ":" + contentType + ":" + contentId;
            cacheService.delete(statusCacheKey);
            
            // 清除用户收藏内容列表缓存
            String userFavoritedCacheKey = CacheConstants.USER_FAVORITED_CONTENT_IDS_CACHE_KEY_PREFIX + userId + ":" + contentType;
            cacheService.delete(userFavoritedCacheKey);
            
            // 清除用户收藏话题/问题列表缓存
            if ("topic".equalsIgnoreCase(contentType)) {
                cacheService.delete(CacheConstants.USER_FAVORITE_TOPICS_CACHE_KEY_PREFIX + userId);
            } else if ("question".equalsIgnoreCase(contentType)) {
                cacheService.delete(CacheConstants.USER_FAVORITE_QUESTIONS_CACHE_KEY_PREFIX + userId);
            }
            
            // 发送通知
            // 获取内容作者ID
            Long authorId = getContentAuthorId(contentType, contentId);
            if (authorId != null && !authorId.equals(userId)) {
                // 获取收藏用户信息
                User user = userService.getById(userId.intValue());
                String userName = user != null ? user.getNickname() : "用户" + userId;
                
                // 构建通知内容
                String contentTypeDesc = getContentTypeDescription(contentType);
                String content = userName + " 收藏了你的" + contentTypeDesc;
                
                // 创建通知
                notificationService.createNotification(authorId, content, "FAVORITE", favorite.getId());
            }
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public boolean unfavoriteContent(Long userId, String contentType, Long contentId) {
        boolean result = favoriteMapper.deleteUserFavorite(userId, contentType, contentId) > 0;
        
        if (result) {
            // 清除收藏计数缓存
            String countCacheKey = CacheConstants.CONTENT_FAVORITE_COUNT_CACHE_KEY_PREFIX + contentType + ":" + contentId;
            cacheService.delete(countCacheKey);
            
            // 清除用户收藏状态缓存
            String statusCacheKey = CacheConstants.USER_FAVORITE_STATUS_CACHE_KEY_PREFIX + userId + ":" + contentType + ":" + contentId;
            cacheService.delete(statusCacheKey);
            
            // 清除用户收藏内容列表缓存
            String userFavoritedCacheKey = CacheConstants.USER_FAVORITED_CONTENT_IDS_CACHE_KEY_PREFIX + userId + ":" + contentType;
            cacheService.delete(userFavoritedCacheKey);
            
            // 清除用户收藏话题/问题列表缓存
            if ("topic".equalsIgnoreCase(contentType)) {
                cacheService.delete(CacheConstants.USER_FAVORITE_TOPICS_CACHE_KEY_PREFIX + userId);
            } else if ("question".equalsIgnoreCase(contentType)) {
                cacheService.delete(CacheConstants.USER_FAVORITE_QUESTIONS_CACHE_KEY_PREFIX + userId);
            }
        }
        
        return result;
    }
    
    @Override
    public boolean checkUserFavorited(Long userId, String contentType, Long contentId) {
        // 尝试从缓存获取
        String cacheKey = CacheConstants.USER_FAVORITE_STATUS_CACHE_KEY_PREFIX + userId + ":" + contentType + ":" + contentId;
        Boolean cachedStatus = cacheService.get(cacheKey, Boolean.class);
        if (cachedStatus != null) {
            return cachedStatus;
        }
        
        // 从数据库查询
        boolean favorited = favoriteMapper.checkUserFavorited(userId, contentType, contentId) > 0;
        
        // 缓存结果
        cacheService.set(cacheKey, favorited, CacheConstants.FAVORITE_STATUS_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return favorited;
    }
    
    @Override
    public List<Long> getUserFavoritedContentIds(Long userId, String contentType) {
        // 尝试从缓存获取
        String cacheKey = CacheConstants.USER_FAVORITED_CONTENT_IDS_CACHE_KEY_PREFIX + userId + ":" + contentType;
        List<Long> cachedIds = cacheService.getList(cacheKey, Long.class);
        if (cachedIds != null) {
            return cachedIds;
        }
        
        // 从数据库查询
        List<Long> contentIds = favoriteMapper.selectUserFavoritedContentIds(userId, contentType);
        
        // 缓存结果
        cacheService.setList(cacheKey, contentIds, CacheConstants.FAVORITE_STATUS_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return contentIds;
    }
    
    @Override
    public Page<CommunityFavorite> getUserFavorites(Long userId, String contentType, int page, int size) {
        Page<CommunityFavorite> pageSetting = new Page<>(page, size);
        return favoriteMapper.selectUserFavorites(pageSetting, userId, contentType);
    }
    
    @Override
    public int getContentFavoriteCount(String contentType, Long contentId) {
        // 尝试从缓存获取
        String cacheKey = CacheConstants.CONTENT_FAVORITE_COUNT_CACHE_KEY_PREFIX + contentType + ":" + contentId;
        Integer cachedCount = cacheService.get(cacheKey, Integer.class);
        if (cachedCount != null) {
            return cachedCount;
        }
        
        // 从数据库查询
        int count = favoriteMapper.countFavoritesByContent(contentType, contentId);
        
        // 缓存结果
        cacheService.set(cacheKey, count, CacheConstants.FAVORITE_STATUS_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return count;
    }
    
    @Override
    public List<Long> batchCheckUserFavorited(Long userId, String contentType, List<Long> contentIds) {
        if (contentIds == null || contentIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 对于批量查询，直接查询数据库更高效
        LambdaQueryWrapper<CommunityFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityFavorite::getUserId, userId)
                  .eq(CommunityFavorite::getContentType, contentType)
                  .in(CommunityFavorite::getContentId, contentIds);
        
        List<CommunityFavorite> favorites = list(queryWrapper);
        return favorites.stream().map(CommunityFavorite::getContentId).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public boolean favorite(Long userId, Long targetId, String targetType) {
        return favoriteContent(userId, targetType, targetId);
    }
    
    @Override
    @Transactional
    public void unfavorite(Long userId, Long targetId, String targetType) {
        unfavoriteContent(userId, targetType, targetId);
    }
    
    @Override
    public boolean checkFavorite(Long userId, Long targetId, String targetType) {
        return checkUserFavorited(userId, targetType, targetId);
    }
    
    @Override
    public Page<TopicVO> getFavoriteTopics(Long userId, int page, int size) {
        // 尝试从缓存获取第一页数据
        if (page == 1) {
            String cacheKey = CacheConstants.USER_FAVORITE_TOPICS_CACHE_KEY_PREFIX + userId + ":" + size;
            Page<TopicVO> cachedPage = cacheService.get(cacheKey, Page.class);
            if (cachedPage != null) {
                log.debug("从缓存获取用户收藏话题列表: {}", userId);
                return cachedPage;
            }
        }
        
        // 获取用户收藏的话题ID列表
        List<Long> topicIds = getUserFavoritedContentIds(userId, "topic");
        if (topicIds.isEmpty()) {
            return new Page<>(page, size, 0);
        }
        
        // 查询话题详情
        Page<CommunityTopic> topicPage = new Page<>(page, size);
        LambdaQueryWrapper<CommunityTopic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CommunityTopic::getId, topicIds)
                   .orderByDesc(CommunityTopic::getCreatedAt);
        
        Page<CommunityTopic> topics = topicMapper.selectPage(topicPage, queryWrapper);
        
        // 转换为VO
        List<TopicVO> topicVOs = new ArrayList<>();
        for (CommunityTopic topic : topics.getRecords()) {
            TopicVO vo = new TopicVO();
            // TODO: 完善VO转换逻辑，需要补充用户信息、标签等
            vo.setId(topic.getId());
            vo.setTitle(topic.getTitle());
            vo.setContent(topic.getContent());
            vo.setViewCount(topic.getViewCount());
            vo.setLikeCount(likeService.getContentLikeCount("topic", topic.getId()));
            // 注意：TopicVO 类中没有 favoriteCount 属性
            vo.setCommentCount(0); // TODO: 需要查询评论数量
            vo.setCreatedAt(topic.getCreatedAt());
            vo.setUpdatedAt(topic.getUpdatedAt());
            vo.setFavorited(true); // 当前用户肯定收藏了
            topicVOs.add(vo);
        }
        
        // 构造返回结果
        Page<TopicVO> result = new Page<>(page, size, topics.getTotal());
        result.setRecords(topicVOs);
        
        // 缓存第一页结果
        if (page == 1) {
            String cacheKey = CacheConstants.USER_FAVORITE_TOPICS_CACHE_KEY_PREFIX + userId + ":" + size;
            cacheService.set(cacheKey, result, CacheConstants.USER_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return result;
    }
    
    @Override
    public Page<QuestionVO> getFavoriteQuestions(Long userId, int page, int size) {
        // 尝试从缓存获取第一页数据
        if (page == 1) {
            String cacheKey = CacheConstants.USER_FAVORITE_QUESTIONS_CACHE_KEY_PREFIX + userId + ":" + size;
            Page<QuestionVO> cachedPage = cacheService.get(cacheKey, Page.class);
            if (cachedPage != null) {
                log.debug("从缓存获取用户收藏问题列表: {}", userId);
                return cachedPage;
            }
        }
        
        // 获取用户收藏的问题ID列表
        List<Long> questionIds = getUserFavoritedContentIds(userId, "question");
        if (questionIds.isEmpty()) {
            return new Page<>(page, size, 0);
        }
        
        // 查询问题详情
        Page<CommunityQuestion> questionPage = new Page<>(page, size);
        LambdaQueryWrapper<CommunityQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CommunityQuestion::getId, questionIds)
                   .orderByDesc(CommunityQuestion::getCreatedAt);
        
        Page<CommunityQuestion> questions = questionMapper.selectPage(questionPage, queryWrapper);
        
        // 转换为VO
        List<QuestionVO> questionVOs = new ArrayList<>();
        for (CommunityQuestion question : questions.getRecords()) {
            QuestionVO vo = new QuestionVO();
            // TODO: 完善VO转换逻辑，需要补充用户信息、标签等
            vo.setId(question.getId());
            vo.setTitle(question.getTitle());
            vo.setContent(question.getContent());
            vo.setViewCount(question.getViewCount());
            // QuestionVO 中没有 likeCount 属性，可能需要添加
            // 注意：QuestionVO 类中没有 favoriteCount 属性
            vo.setAnswerCount(question.getAnswerCount());
            vo.setCreatedAt(question.getCreatedAt());
            vo.setUpdatedAt(question.getUpdatedAt());
            vo.setStatus(question.getStatus()); // 使用 status 而不是 solved
            vo.setFavorited(true); // 当前用户肯定收藏了
            questionVOs.add(vo);
        }
        
        // 构造返回结果
        Page<QuestionVO> result = new Page<>(page, size, questions.getTotal());
        result.setRecords(questionVOs);
        
        // 缓存第一页结果
        if (page == 1) {
            String cacheKey = CacheConstants.USER_FAVORITE_QUESTIONS_CACHE_KEY_PREFIX + userId + ":" + size;
            cacheService.set(cacheKey, result, CacheConstants.USER_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return result;
    }

    /**
     * 获取内容作者ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 作者ID
     */
    private Long getContentAuthorId(String contentType, Long contentId) {
        if (contentType == null || contentId == null) {
            return null;
        }
        
        switch (contentType.toLowerCase()) {
            case "topic":
                CommunityTopic topic = topicMapper.selectById(contentId);
                return topic != null ? topic.getUserId() : null;
            case "question":
                CommunityQuestion question = questionMapper.selectById(contentId);
                return question != null ? question.getUserId() : null;
            default:
                return null;
        }
    }
    
    /**
     * 获取内容类型描述
     * @param contentType 内容类型
     * @return 内容类型描述
     */
    private String getContentTypeDescription(String contentType) {
        if (contentType == null) {
            return "内容";
        }
        
        return switch (contentType.toLowerCase()) {
            case "topic" -> "话题";
            case "question" -> "问题";
            case "resource" -> "资源";
            default -> "内容";
        };
    }
} 