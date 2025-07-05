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
import com.csu.unicorp.entity.community.CommunityAnswer;
import com.csu.unicorp.entity.community.CommunityLike;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.community.CommunityAnswerMapper;
import com.csu.unicorp.mapper.community.CommunityLikeMapper;
import com.csu.unicorp.service.CommunityLikeService;
import com.csu.unicorp.service.CommunityNotificationService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.community.AnswerVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 社区点赞Service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityLikeServiceImpl extends ServiceImpl<CommunityLikeMapper, CommunityLike> implements CommunityLikeService {
    
    private final CommunityLikeMapper likeMapper;
    private final CommunityAnswerMapper answerMapper;
    private final CommunityNotificationService notificationService;
    private final UserService userService;
    
    @Override
    @Transactional
    public boolean likeContent(Long userId, String contentType, Long contentId) {
        // 检查是否已经点赞
        if (checkUserLiked(userId, contentType, contentId)) {
            return true;
        }
        
        // 创建点赞记录
        CommunityLike like = CommunityLike.builder()
                .userId(userId)
                .contentType(contentType)
                .contentId(contentId)
                .createdAt(LocalDateTime.now())
                .build();
        
        boolean result = save(like);
        
        // 发送通知
        if (result) {
            // 获取内容作者ID
            Long authorId = getContentAuthorId(contentType, contentId);
            if (authorId != null && !authorId.equals(userId)) {
                // 获取点赞用户信息
                User user = userService.getById(userId.intValue());
                String userName = user != null ? user.getNickname() : "用户" + userId;
                
                // 构建通知内容
                String contentTypeDesc = getContentTypeDescription(contentType);
                String content = userName + " 点赞了你的" + contentTypeDesc;
                
                // 创建通知
                notificationService.createNotification(authorId, content, "LIKE", like.getId());
            }
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public boolean unlikeContent(Long userId, String contentType, Long contentId) {
        return likeMapper.deleteUserLike(userId, contentType, contentId) > 0;
    }
    
    @Override
    public boolean checkUserLiked(Long userId, String contentType, Long contentId) {
        return likeMapper.checkUserLiked(userId, contentType, contentId) > 0;
    }
    
    @Override
    public List<Long> getUserLikedContentIds(Long userId, String contentType) {
        return likeMapper.selectUserLikedContentIds(userId, contentType);
    }
    
    @Override
    public Page<CommunityLike> getUserLikes(Long userId, String contentType, int page, int size) {
        Page<CommunityLike> pageSetting = new Page<>(page, size);
        return likeMapper.selectUserLikes(pageSetting, userId, contentType);
    }
    
    @Override
    public int getContentLikeCount(String contentType, Long contentId) {
        return likeMapper.countLikesByContent(contentType, contentId);
    }
    
    @Override
    public List<Long> batchCheckUserLiked(Long userId, String contentType, List<Long> contentIds) {
        if (contentIds == null || contentIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        LambdaQueryWrapper<CommunityLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityLike::getUserId, userId)
                  .eq(CommunityLike::getContentType, contentType)
                  .in(CommunityLike::getContentId, contentIds);
        
        List<CommunityLike> likes = list(queryWrapper);
        return likes.stream().map(CommunityLike::getContentId).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public boolean like(Long userId, Long targetId, String targetType) {
        return likeContent(userId, targetType, targetId);
    }
    
    @Override
    @Transactional
    public void unlike(Long userId, Long targetId, String targetType) {
        unlikeContent(userId, targetType, targetId);
    }
    
    @Override
    public boolean checkLike(Long userId, Long targetId, String targetType) {
        return checkUserLiked(userId, targetType, targetId);
    }
    
    @Override
    public Page<AnswerVO> getLikedAnswers(Long userId, int page, int size) {
        // 获取用户点赞的回答ID列表
        List<Long> answerIds = getUserLikedContentIds(userId, "answer");
        if (answerIds.isEmpty()) {
            return new Page<>(page, size, 0);
        }
        
        // 查询回答详情
        Page<CommunityAnswer> answerPage = new Page<>(page, size);
        LambdaQueryWrapper<CommunityAnswer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CommunityAnswer::getId, answerIds)
                   .orderByDesc(CommunityAnswer::getCreatedAt);
        
        Page<CommunityAnswer> answers = answerMapper.selectPage(answerPage, queryWrapper);
        
        // 转换为VO
        List<AnswerVO> answerVOs = new ArrayList<>();
        for (CommunityAnswer answer : answers.getRecords()) {
            AnswerVO vo = new AnswerVO();
            // TODO: 完善VO转换逻辑，需要补充用户信息、问题信息等
            vo.setId(answer.getId());
            vo.setContent(answer.getContent());
            vo.setLikeCount(getContentLikeCount("answer", answer.getId()));
            vo.setLiked(true); // 当前用户肯定点赞了
            // 注意：AnswerVO类中没有commentCount属性
            vo.setCreatedAt(answer.getCreatedAt());
            answerVOs.add(vo);
        }
        
        // 构造返回结果
        Page<AnswerVO> result = new Page<>(page, size, answers.getTotal());
        result.setRecords(answerVOs);
        
        return result;
    }

    /**
     * 获取内容作者ID
     * @param contentType 内容类型
     * @param contentId 内容ID
     * @return 作者ID
     */
    private Long getContentAuthorId(String contentType, Long contentId) {
        if (contentId == null || contentType == null) {
            return null;
        }
        
        try {
            // 将contentType转换为大写
            String type = contentType.toUpperCase();
            return likeMapper.selectContentAuthorId(type, contentId);
        } catch (Exception e) {
            // 记录异常但不抛出，返回null
            log.error("获取内容作者ID失败: contentType={}, contentId={}", contentType, contentId, e);
            return null;
        }
    }
    
    /**
     * 获取内容类型描述
     * @param contentType 内容类型
     * @return 内容类型描述
     */
    private String getContentTypeDescription(String contentType) {
        return switch (contentType.toUpperCase()) {
            case "TOPIC" -> "话题";
            case "COMMENT" -> "评论";
            case "QUESTION" -> "问题";
            case "ANSWER" -> "回答";
            default -> "内容";
        };
    }
} 