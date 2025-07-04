package com.csu.unicorp.service.impl.community;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.community.CommunityFavorite;
import com.csu.unicorp.entity.community.CommunityQuestion;
import com.csu.unicorp.entity.community.CommunityTopic;
import com.csu.unicorp.mapper.community.CommunityFavoriteMapper;
import com.csu.unicorp.mapper.community.CommunityQuestionMapper;
import com.csu.unicorp.mapper.community.CommunityTopicMapper;
import com.csu.unicorp.service.CommunityFavoriteService;
import com.csu.unicorp.service.CommunityLikeService;
import com.csu.unicorp.service.CommunityNotificationService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.community.QuestionVO;
import com.csu.unicorp.vo.community.TopicVO;

import lombok.RequiredArgsConstructor;

/**
 * 社区收藏Service实现类
 */
@Service
@RequiredArgsConstructor
public class CommunityFavoriteServiceImpl extends ServiceImpl<CommunityFavoriteMapper, CommunityFavorite> implements CommunityFavoriteService {
    
    private final CommunityFavoriteMapper favoriteMapper;
    private final CommunityTopicMapper topicMapper;
    private final CommunityQuestionMapper questionMapper;
    private final CommunityLikeService likeService;
    private final CommunityNotificationService notificationService;
    private final UserService userService;
    
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
        
        // 发送通知
        if (result) {
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
        return favoriteMapper.deleteUserFavorite(userId, contentType, contentId) > 0;
    }
    
    @Override
    public boolean checkUserFavorited(Long userId, String contentType, Long contentId) {
        return favoriteMapper.checkUserFavorited(userId, contentType, contentId) > 0;
    }
    
    @Override
    public List<Long> getUserFavoritedContentIds(Long userId, String contentType) {
        return favoriteMapper.selectUserFavoritedContentIds(userId, contentType);
    }
    
    @Override
    public Page<CommunityFavorite> getUserFavorites(Long userId, String contentType, int page, int size) {
        Page<CommunityFavorite> pageSetting = new Page<>(page, size);
        return favoriteMapper.selectUserFavorites(pageSetting, userId, contentType);
    }
    
    @Override
    public int getContentFavoriteCount(String contentType, Long contentId) {
        return favoriteMapper.countFavoritesByContent(contentType, contentId);
    }
    
    @Override
    public List<Long> batchCheckUserFavorited(Long userId, String contentType, List<Long> contentIds) {
        if (contentIds == null || contentIds.isEmpty()) {
            return Collections.emptyList();
        }
        
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
        
        return result;
    }
    
    @Override
    public Page<QuestionVO> getFavoriteQuestions(Long userId, int page, int size) {
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