package com.csu.unicorp.service.impl.community;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.constants.CacheConstants;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.dto.community.AnswerDTO;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.community.CommunityAnswer;
import com.csu.unicorp.mapper.community.CommunityAnswerMapper;
import com.csu.unicorp.service.CacheService;
import com.csu.unicorp.service.CommunityAnswerService;
import com.csu.unicorp.service.CommunityLikeService;
import com.csu.unicorp.service.CommunityQuestionService;
import com.csu.unicorp.service.FileService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.community.AnswerVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 社区回答Service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityAnswerServiceImpl extends ServiceImpl<CommunityAnswerMapper, CommunityAnswer>
        implements CommunityAnswerService {

    private final CommunityQuestionService questionService;
    private final CommunityLikeService likeService;
    private final UserService userService;
    private final FileService fileService;
    private final CacheService cacheService;
    
    @Override
    @Transactional
    public Long createAnswer(Long userId, AnswerDTO answerDTO) {
        CommunityAnswer answer = CommunityAnswer.builder()
                .content(answerDTO.getContent())
                .userId(userId)
                .questionId(answerDTO.getQuestionId())
                .likeCount(0)
                .isAccepted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        save(answer);
        
        // 更新问题的回答数量
        questionService.incrementAnswerCount(answerDTO.getQuestionId());
        
        // 清除相关缓存
        cacheService.delete(CacheConstants.QUESTION_ANSWERS_CACHE_KEY_PREFIX + answerDTO.getQuestionId());
        cacheService.delete(CacheConstants.USER_ANSWERS_CACHE_KEY_PREFIX + userId);
        
        return answer.getId();
    }

    @Override
    public AnswerVO getAnswerDetail(Long answerId, Long userId) {
        // 直接从数据库获取最新数据
        CommunityAnswer answer = getById(answerId);
        if (answer == null) {
            return null;
        }
        
        AnswerVO answerVO = convertToVO(answer, userId);
        
        // 缓存回答详情
        String cacheKey = CacheConstants.ANSWER_DETAIL_CACHE_KEY_PREFIX + answerId;
        cacheService.set(cacheKey, answerVO, CacheConstants.ANSWER_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return answerVO;
    }

    @Override
    @Transactional
    public boolean updateAnswer(Long userId, Long answerId, AnswerDTO answerDTO) {
        CommunityAnswer answer = getById(answerId);
        if (answer == null) {
            return false;
        }
        
        answer.setContent(answerDTO.getContent());
        answer.setUpdatedAt(LocalDateTime.now());
        
        boolean result = updateById(answer);
        
        // 清除相关缓存
        if (result) {
            cacheService.delete(CacheConstants.ANSWER_DETAIL_CACHE_KEY_PREFIX + answerId);
            cacheService.delete(CacheConstants.QUESTION_ANSWERS_CACHE_KEY_PREFIX + answer.getQuestionId());
            cacheService.delete(CacheConstants.USER_ANSWERS_CACHE_KEY_PREFIX + userId);
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean deleteAnswer(Long userId, Long answerId) {
        CommunityAnswer answer = getById(answerId);
        if (answer != null) {
            boolean removed = removeById(answerId);
            if (removed) {
                // 减少问题的回答数量
                questionService.decrementAnswerCount(answer.getQuestionId());
                
                // 清除相关缓存
                cacheService.delete(CacheConstants.ANSWER_DETAIL_CACHE_KEY_PREFIX + answerId);
                cacheService.delete(CacheConstants.QUESTION_ANSWERS_CACHE_KEY_PREFIX + answer.getQuestionId());
                cacheService.delete(CacheConstants.USER_ANSWERS_CACHE_KEY_PREFIX + answer.getUserId());
                
                // 清除回答相关的评论缓存
                cacheService.delete(CacheConstants.ANSWER_COMMENTS_CACHE_KEY_PREFIX + answerId);
            }
            return removed;
        }
        return false;
    }

    @Override
    public boolean checkAnswerEditPermission(Long userId, Long answerId) {
        CommunityAnswer answer = getById(answerId);
        
        // 检查用户是否是管理员
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            // 系统管理员有所有权限
            if (userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + RoleConstants.ROLE_SYSTEM_ADMIN))) {
                return true;
            }
        }
        
        return answer != null && answer.getUserId().equals(userId);
    }

    @Override
    public Page<AnswerVO> getAnswersByQuestion(Long questionId, Integer page, Integer size, Long userId) {
        // 对于分页数据，只缓存第一页
        if (page == 1) {
            String cacheKey = CacheConstants.QUESTION_ANSWERS_CACHE_KEY_PREFIX + questionId + ":" + size;
            Page<AnswerVO> cachedPage = cacheService.get(cacheKey, Page.class);
            if (cachedPage != null) {
                log.debug("从缓存获取问题回答列表: {}", questionId);
                // 更新用户交互状态
                if (userId != null && cachedPage.getRecords() != null) {
                    for (AnswerVO answer : cachedPage.getRecords()) {
                        answer.setLiked(likeService.checkLike(userId, answer.getId(), "ANSWER"));
                    }
                }
                return cachedPage;
            }
        }
        
        Page<CommunityAnswer> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<CommunityAnswer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityAnswer::getQuestionId, questionId)
                   .orderByDesc(CommunityAnswer::getIsAccepted)
                   .orderByDesc(CommunityAnswer::getLikeCount)
                   .orderByDesc(CommunityAnswer::getCreatedAt);
        
        Page<CommunityAnswer> answerPage = page(pageParam, queryWrapper);
        
        Page<AnswerVO> voPage = new Page<>(answerPage.getCurrent(), answerPage.getSize(), answerPage.getTotal());
        voPage.setRecords(answerPage.getRecords().stream()
                .map(answer -> convertToVO(answer, userId))
                .toList());
        
        // 缓存第一页数据
        if (page == 1) {
            String cacheKey = CacheConstants.QUESTION_ANSWERS_CACHE_KEY_PREFIX + questionId + ":" + size;
            // 缓存不包含用户交互状态的数据
            Page<AnswerVO> cacheData = new Page<>(voPage.getCurrent(), voPage.getSize(), voPage.getTotal());
            cacheData.setRecords(answerPage.getRecords().stream()
                    .map(answer -> convertToVO(answer, null))
                    .toList());
            cacheService.set(cacheKey, cacheData, CacheConstants.ANSWER_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return voPage;
    }

    @Override
    public Page<AnswerVO> getUserAnswers(Long userId, Integer page, Integer size, Long currentUserId) {
        // 对于分页数据，只缓存第一页
        if (page == 1) {
            String cacheKey = CacheConstants.USER_ANSWERS_CACHE_KEY_PREFIX + userId + ":" + size;
            Page<AnswerVO> cachedPage = cacheService.get(cacheKey, Page.class);
            if (cachedPage != null) {
                log.debug("从缓存获取用户回答列表: {}", userId);
                // 更新用户交互状态
                if (currentUserId != null && cachedPage.getRecords() != null) {
                    for (AnswerVO answer : cachedPage.getRecords()) {
                        answer.setLiked(likeService.checkLike(currentUserId, answer.getId(), "ANSWER"));
                    }
                }
                return cachedPage;
            }
        }
        
        Page<CommunityAnswer> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<CommunityAnswer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityAnswer::getUserId, userId)
                   .orderByDesc(CommunityAnswer::getCreatedAt);
        
        Page<CommunityAnswer> answerPage = page(pageParam, queryWrapper);
        
        Page<AnswerVO> voPage = new Page<>(answerPage.getCurrent(), answerPage.getSize(), answerPage.getTotal());
        voPage.setRecords(answerPage.getRecords().stream()
                .map(answer -> convertToVO(answer, currentUserId))
                .toList());
        
        // 缓存第一页数据
        if (page == 1) {
            String cacheKey = CacheConstants.USER_ANSWERS_CACHE_KEY_PREFIX + userId + ":" + size;
            // 缓存不包含用户交互状态的数据
            Page<AnswerVO> cacheData = new Page<>(voPage.getCurrent(), voPage.getSize(), voPage.getTotal());
            cacheData.setRecords(answerPage.getRecords().stream()
                    .map(answer -> convertToVO(answer, null))
                    .toList());
            cacheService.set(cacheKey, cacheData, CacheConstants.USER_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return voPage;
    }
    
    @Override
    @Transactional
    public boolean markAsAccepted(Long answerId) {
        CommunityAnswer answer = getById(answerId);
        if (answer == null) {
            return false;
        }
        
        // 更新为已接受
        answer.setIsAccepted(true);
        answer.setUpdatedAt(LocalDateTime.now());
        
        boolean result = updateById(answer);
        
        // 清除相关缓存
        if (result) {
            cacheService.delete(CacheConstants.ANSWER_DETAIL_CACHE_KEY_PREFIX + answerId);
            cacheService.delete(CacheConstants.QUESTION_ANSWERS_CACHE_KEY_PREFIX + answer.getQuestionId());
            
            // 标记问题为已解决
            questionService.markQuestionSolved(answer.getQuestionId(), answerId);
        }
        
        return result;
    }
    
    /**
     * 将实体转换为VO
     * @param answer 回答实体
     * @param userId 当前用户ID（可选）
     * @return 回答VO
     */
    private AnswerVO convertToVO(CommunityAnswer answer, Long userId) {
        if (answer == null) {
            return null;
        }
        
        // 获取用户信息
        User user = userService.getById(answer.getUserId().intValue());
        String userName = "用户" + answer.getUserId();
        String userAvatar = "/avatars/default/avatar.jpg";
        
        if (user != null) {
            userName = user.getNickname();
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                userAvatar = fileService.getFullFileUrl(user.getAvatar());
            }
        }
        
        AnswerVO vo = AnswerVO.builder()
                .id(answer.getId())
                .content(answer.getContent())
                .userId(answer.getUserId())
                .userName(userName)
                .userAvatar(userAvatar)
                .questionId(answer.getQuestionId())
                .likeCount(answer.getLikeCount())
                .isAccepted(answer.getIsAccepted())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .build();
        
        // 检查当前用户是否已点赞
        if (userId != null) {
            vo.setLiked(likeService.checkLike(userId, answer.getId(), "ANSWER"));
        }
        
        return vo;
    }
} 