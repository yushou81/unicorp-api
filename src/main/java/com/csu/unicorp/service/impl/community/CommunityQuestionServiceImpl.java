package com.csu.unicorp.service.impl.community;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.constants.CacheConstants;
import com.csu.unicorp.dto.community.QuestionDTO;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.entity.community.CommunityContentTag;
import com.csu.unicorp.entity.community.CommunityQuestion;
import com.csu.unicorp.mapper.community.CommunityAnswerMapper;
import com.csu.unicorp.mapper.community.CommunityContentTagMapper;
import com.csu.unicorp.mapper.community.CommunityQuestionMapper;
import com.csu.unicorp.service.CacheService;
import com.csu.unicorp.service.CommunityFavoriteService;
import com.csu.unicorp.service.CommunityLikeService;
import com.csu.unicorp.service.CommunityNotificationService;
import com.csu.unicorp.service.CommunityQuestionService;
import com.csu.unicorp.service.CommunityTagService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.community.QuestionVO;
import com.csu.unicorp.vo.community.TagVO;

import lombok.RequiredArgsConstructor;

/**
 * 社区问题Service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityQuestionServiceImpl extends ServiceImpl<CommunityQuestionMapper, CommunityQuestion> 
        implements CommunityQuestionService {
    
    private final CommunityQuestionMapper questionMapper;
    private final CommunityContentTagMapper contentTagMapper;
    private final CommunityTagService tagService;
    private final CommunityLikeService likeService;
    private final CommunityFavoriteService favoriteService;
    private final CommunityAnswerMapper answerMapper;
    private final CommunityNotificationService notificationService;
    private final UserService userService;
    private final CacheService cacheService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createQuestion(Long userId, QuestionDTO questionDTO) {
        CommunityQuestion question = new CommunityQuestion();
        question.setTitle(questionDTO.getTitle());
        question.setContent(questionDTO.getContent());
        question.setUserId(userId);
        question.setViewCount(0);
        question.setCategoryId(questionDTO.getCategoryId());
        question.setAnswerCount(0);
        question.setStatus("UNSOLVED");
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());
        
        save(question);
        Long questionId = question.getId();
        
        // 保存问题标签关联
        if (!CollectionUtils.isEmpty(questionDTO.getTagIds())) {
            saveQuestionTags(questionId, questionDTO.getTagIds());
        }
        
        // 清除相关缓存
        cacheService.delete(CacheConstants.LATEST_QUESTIONS_CACHE_KEY);
        cacheService.delete(CacheConstants.UNSOLVED_QUESTIONS_CACHE_KEY);
        if (questionDTO.getCategoryId() != null) {
            cacheService.delete(CacheConstants.CATEGORY_QUESTIONS_CACHE_KEY_PREFIX + questionDTO.getCategoryId());
        }
        
        return questionId;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateQuestion(Long userId, Long questionId, QuestionDTO questionDTO) {
        CommunityQuestion question = getById(questionId);
        if (question == null) {
            return false;
        }
        
        question.setTitle(questionDTO.getTitle());
        question.setContent(questionDTO.getContent());
        question.setUpdatedAt(LocalDateTime.now());
        
        boolean result = updateById(question);
        
        // 更新问题标签关联
        tagService.updateContentTags("QUESTION", questionId, questionDTO.getTagIds());
        
        // 清除相关缓存
        if (result) {
            cacheService.delete(CacheConstants.QUESTION_DETAIL_CACHE_KEY_PREFIX + questionId);
            
            // 如果分类发生变化，清除相关分类问题缓存
            if (questionDTO.getCategoryId() != null && !questionDTO.getCategoryId().equals(question.getCategoryId())) {
                cacheService.delete(CacheConstants.CATEGORY_QUESTIONS_CACHE_KEY_PREFIX + question.getCategoryId());
                cacheService.delete(CacheConstants.CATEGORY_QUESTIONS_CACHE_KEY_PREFIX + questionDTO.getCategoryId());
            }
            
            // 清除热门和最新问题缓存
            cacheService.delete(CacheConstants.HOT_QUESTIONS_CACHE_KEY);
            cacheService.delete(CacheConstants.LATEST_QUESTIONS_CACHE_KEY);
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteQuestion(Long userId, Long questionId) {
        CommunityQuestion question = getById(questionId);
        if (question != null) {
            boolean result = removeById(questionId);
            
            if (result) {
                // 清除相关缓存
                cacheService.delete(CacheConstants.QUESTION_DETAIL_CACHE_KEY_PREFIX + questionId);
                cacheService.delete(CacheConstants.HOT_QUESTIONS_CACHE_KEY);
                cacheService.delete(CacheConstants.LATEST_QUESTIONS_CACHE_KEY);
                cacheService.delete(CacheConstants.UNSOLVED_QUESTIONS_CACHE_KEY);
                
                if (question.getCategoryId() != null) {
                    cacheService.delete(CacheConstants.CATEGORY_QUESTIONS_CACHE_KEY_PREFIX + question.getCategoryId());
                }
                
                // 清除相关问题缓存
                cacheService.delete(CacheConstants.RELATED_QUESTIONS_CACHE_KEY_PREFIX + questionId);
                
                // 清除用户问题列表缓存
                cacheService.delete(CacheConstants.USER_QUESTIONS_CACHE_KEY_PREFIX + question.getUserId());
            }
            
            return result;
        }
        
        return false;
    }
    
    @Override
    public QuestionVO getQuestionDetail(Long questionId, Long userId) {
        // 尝试从缓存获取
        String cacheKey = CacheConstants.QUESTION_DETAIL_CACHE_KEY_PREFIX + questionId;
        QuestionVO cachedQuestion = cacheService.get(cacheKey, QuestionVO.class);
        if (cachedQuestion != null) {
            log.debug("从缓存获取问题详情: {}", questionId);
            // 更新用户交互状态
            if (userId != null) {
                cachedQuestion.setFavorited(checkUserFavoritedQuestion(userId, questionId));
            } else {
                cachedQuestion.setFavorited(false);
            }
            return cachedQuestion;
        }
        
        CommunityQuestion question = getById(questionId);
        if (question == null) {
            return null;
        }
        
        QuestionVO questionVO = convertToQuestionVO(question, null);
        
        // 缓存问题详情，不包含用户交互状态
        cacheService.set(cacheKey, questionVO, CacheConstants.QUESTION_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        // 设置用户交互状态
        if (userId != null) {
            questionVO.setFavorited(checkUserFavoritedQuestion(userId, questionId));
        }
        
        return questionVO;
    }
    
    @Override
    public Page<QuestionVO> getQuestionsByCategory(Long categoryId, int page, int size, Long userId) {
        // 对于分页数据，只缓存第一页
        if (page == 1) {
            String cacheKey = CacheConstants.CATEGORY_QUESTIONS_CACHE_KEY_PREFIX + categoryId + ":" + size;
            Page<QuestionVO> cachedPage = cacheService.get(cacheKey, Page.class);
            if (cachedPage != null) {
                log.debug("从缓存获取分类问题列表: {}", categoryId);
                // 更新用户交互状态
                if (userId != null) {
                    updateUserInteractionStatus(cachedPage.getRecords(), userId);
                }
                return cachedPage;
            }
        }
        
        Page<CommunityQuestion> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<CommunityQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityQuestion::getCategoryId, categoryId)
                   .orderByDesc(CommunityQuestion::getCreatedAt);
        
        Page<CommunityQuestion> questionPage = page(pageParam, queryWrapper);
        
        Page<QuestionVO> result = convertToQuestionVOPage(questionPage, userId);
        
        // 缓存第一页数据
        if (page == 1) {
            String cacheKey = CacheConstants.CATEGORY_QUESTIONS_CACHE_KEY_PREFIX + categoryId + ":" + size;
            // 缓存不包含用户交互状态的数据
            Page<QuestionVO> cacheData = convertToQuestionVOPage(questionPage, null);
            cacheService.set(cacheKey, cacheData, CacheConstants.QUESTION_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return result;
    }
    
    @Override
    public Page<QuestionVO> getHotQuestions(int page, int size, Long userId) {
        // 对于分页数据，只缓存第一页
        if (page == 1) {
            String cacheKey = CacheConstants.HOT_QUESTIONS_CACHE_KEY + ":" + size;
            Page<QuestionVO> cachedPage = cacheService.get(cacheKey, Page.class);
            if (cachedPage != null) {
                log.debug("从缓存获取热门问题列表");
                // 更新用户交互状态
                if (userId != null) {
                    updateUserInteractionStatus(cachedPage.getRecords(), userId);
                }
                return cachedPage;
            }
        }
        
        Page<CommunityQuestion> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<CommunityQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(CommunityQuestion::getViewCount)
                   .orderByDesc(CommunityQuestion::getAnswerCount);
        
        Page<CommunityQuestion> questionPage = page(pageParam, queryWrapper);
        
        Page<QuestionVO> result = convertToQuestionVOPage(questionPage, userId);
        
        // 缓存第一页数据
        if (page == 1) {
            String cacheKey = CacheConstants.HOT_QUESTIONS_CACHE_KEY + ":" + size;
            // 缓存不包含用户交互状态的数据
            Page<QuestionVO> cacheData = convertToQuestionVOPage(questionPage, null);
            cacheService.set(cacheKey, cacheData, CacheConstants.HOT_QUESTIONS_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return result;
    }
    
    @Override
    public Page<QuestionVO> getLatestQuestions(int page, int size, Long userId) {
        // 对于分页数据，只缓存第一页
        if (page == 1) {
            String cacheKey = CacheConstants.LATEST_QUESTIONS_CACHE_KEY + ":" + size;
            Page<QuestionVO> cachedPage = cacheService.get(cacheKey, Page.class);
            if (cachedPage != null) {
                log.debug("从缓存获取最新问题列表");
                // 更新用户交互状态
                if (userId != null) {
                    updateUserInteractionStatus(cachedPage.getRecords(), userId);
                }
                return cachedPage;
            }
        }
        
        Page<CommunityQuestion> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<CommunityQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(CommunityQuestion::getCreatedAt);
        
        Page<CommunityQuestion> questionPage = page(pageParam, queryWrapper);
        
        Page<QuestionVO> result = convertToQuestionVOPage(questionPage, userId);
        
        // 缓存第一页数据
        if (page == 1) {
            String cacheKey = CacheConstants.LATEST_QUESTIONS_CACHE_KEY + ":" + size;
            // 缓存不包含用户交互状态的数据
            Page<QuestionVO> cacheData = convertToQuestionVOPage(questionPage, null);
            cacheService.set(cacheKey, cacheData, CacheConstants.QUESTION_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return result;
    }
    
    @Override
    public Page<QuestionVO> getBountyQuestions(int page, int size, Long userId) {
        // 实现逻辑，暂时返回空结果
        return new Page<>(page, size, 0);
    }
    
    @Override
    public Page<QuestionVO> getUserQuestions(Long userId, int page, int size, Long currentUserId) {
        // 对于分页数据，只缓存第一页
        if (page == 1) {
            String cacheKey = CacheConstants.USER_QUESTIONS_CACHE_KEY_PREFIX + userId + ":" + size;
            Page<QuestionVO> cachedPage = cacheService.get(cacheKey, Page.class);
            if (cachedPage != null) {
                log.debug("从缓存获取用户问题列表: {}", userId);
                // 更新用户交互状态
                if (currentUserId != null) {
                    updateUserInteractionStatus(cachedPage.getRecords(), currentUserId);
                }
                return cachedPage;
            }
        }
        
        Page<CommunityQuestion> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<CommunityQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityQuestion::getUserId, userId)
                   .orderByDesc(CommunityQuestion::getCreatedAt);
        
        Page<CommunityQuestion> questionPage = page(pageParam, queryWrapper);
        
        Page<QuestionVO> result = convertToQuestionVOPage(questionPage, currentUserId);
        
        // 缓存第一页数据
        if (page == 1) {
            String cacheKey = CacheConstants.USER_QUESTIONS_CACHE_KEY_PREFIX + userId + ":" + size;
            // 缓存不包含用户交互状态的数据
            Page<QuestionVO> cacheData = convertToQuestionVOPage(questionPage, null);
            cacheService.set(cacheKey, cacheData, CacheConstants.USER_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setBestAnswer(Long userId, Long questionId, Long answerId) {
        CommunityQuestion question = getById(questionId);
        if (question == null || !question.getUserId().equals(userId)) {
            return false;
        }
        
        // 直接更新答案状态为已接受
        answerMapper.updateAcceptedStatus(answerId, true);
        
        question.setBestAnswerId(answerId);
        question.setStatus("SOLVED");
        question.setUpdatedAt(LocalDateTime.now());
        
        boolean result = updateById(question);
        
        // 清除相关缓存
        if (result) {
            cacheService.delete(CacheConstants.QUESTION_DETAIL_CACHE_KEY_PREFIX + questionId);
            cacheService.delete(CacheConstants.UNSOLVED_QUESTIONS_CACHE_KEY);
            
            // 发送通知
            // 获取回答作者ID
            Long answerAuthorId = getAnswerAuthorId(answerId);
            if (answerAuthorId != null && !answerAuthorId.equals(userId)) {
                // 获取问题作者信息
                User user = userService.getById(userId.intValue());
                String userName = user != null ? user.getNickname() : "用户" + userId;
                
                // 构建通知内容
                String content = userName + " 采纳了你的回答为最佳答案";
                
                // 创建通知
                notificationService.createNotification(answerAuthorId, content, "ANSWER_ACCEPTED", answerId);
            }
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeQuestion(Long userId, Long questionId) {
        CommunityQuestion question = getById(questionId);
        if (question == null || !question.getUserId().equals(userId)) {
            return false;
        }
        
        question.setStatus("CLOSED");
        question.setUpdatedAt(LocalDateTime.now());
        
        boolean result = updateById(question);
        
        // 清除相关缓存
        if (result) {
            cacheService.delete(CacheConstants.QUESTION_DETAIL_CACHE_KEY_PREFIX + questionId);
            cacheService.delete(CacheConstants.UNSOLVED_QUESTIONS_CACHE_KEY);
        }
        
        return result;
    }
    
    @Override
    public void incrementViewCount(Long questionId) {
        CommunityQuestion question = getById(questionId);
        if (question != null) {
            question.setViewCount(question.getViewCount() + 1);
            updateById(question);
        }
    }
    
    @Override
    public Page<QuestionVO> searchQuestions(String keyword, int page, int size, Long userId) {
        Page<CommunityQuestion> pageParam = new Page<>(page, size);
        
        LambdaQueryWrapper<CommunityQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(CommunityQuestion::getTitle, keyword)
                   .or()
                   .like(CommunityQuestion::getContent, keyword)
                   .orderByDesc(CommunityQuestion::getCreatedAt);
        
        Page<CommunityQuestion> questionPage = page(pageParam, queryWrapper);
        
        return convertToQuestionVOPage(questionPage, userId);
    }
    
    @Override
    public List<QuestionVO> getRecommendQuestions(Long userId, int limit) {
        // 简单实现，获取最热门的问题
        Page<CommunityQuestion> pageParam = new Page<>(1, limit);
        
        LambdaQueryWrapper<CommunityQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(CommunityQuestion::getViewCount)
                   .orderByDesc(CommunityQuestion::getAnswerCount);
        
        Page<CommunityQuestion> questionPage = page(pageParam, queryWrapper);
        
        return questionPage.getRecords().stream()
                .map(question -> convertToQuestionVO(question, userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean checkQuestionEditPermission(Long userId, Long questionId) {
        CommunityQuestion question = getById(questionId);
        if (question == null) {
            return false;
        }
        
        // 问题作者或管理员可以编辑
        // TODO: 实际项目中应该通过权限系统判断是否是管理员
        return question.getUserId().equals(userId) || isAdmin(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean favoriteQuestion(Long userId, Long questionId) {
        return favoriteService.favorite(userId, questionId, "question");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfavoriteQuestion(Long userId, Long questionId) {
        favoriteService.unfavorite(userId, questionId, "question");
        return true;
    }
    
    @Override
    public boolean checkUserFavoritedQuestion(Long userId, Long questionId) {
        return favoriteService.checkFavorite(userId, questionId, "question");
    }
    
    @Override
    public boolean existsById(Long questionId) {
        return getById(questionId) != null;
    }
    
    @Override
    public Page<QuestionVO> getUnsolvedQuestions(Integer page, Integer size, Long userId) {
        // 对于分页数据，只缓存第一页
        if (page == 1) {
            String cacheKey = CacheConstants.UNSOLVED_QUESTIONS_CACHE_KEY + ":" + size;
            Page<QuestionVO> cachedPage = cacheService.get(cacheKey, Page.class);
            if (cachedPage != null) {
                log.debug("从缓存获取未解决问题列表");
                // 更新用户交互状态
                if (userId != null) {
                    updateUserInteractionStatus(cachedPage.getRecords(), userId);
                }
                return cachedPage;
            }
        }
        
        // 创建分页对象
        Page<CommunityQuestion> pageParam = new Page<>(page, size);
        
        // 构建查询条件：未解决（没有最佳答案）的问题
        LambdaQueryWrapper<CommunityQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNull(CommunityQuestion::getBestAnswerId)
                   .eq(CommunityQuestion::getStatus, "UNSOLVED")
                   .orderByDesc(CommunityQuestion::getCreatedAt);
        
        // 执行分页查询
        Page<CommunityQuestion> questionPage = page(pageParam, queryWrapper);
        
        // 转换为VO
        Page<QuestionVO> result = convertToQuestionVOPage(questionPage, userId);
        
        // 缓存第一页数据
        if (page == 1) {
            String cacheKey = CacheConstants.UNSOLVED_QUESTIONS_CACHE_KEY + ":" + size;
            // 缓存不包含用户交互状态的数据
            Page<QuestionVO> cacheData = convertToQuestionVOPage(questionPage, null);
            cacheService.set(cacheKey, cacheData, CacheConstants.QUESTION_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
        return result;
    }
    
    @Override
    public boolean isQuestionAuthor(Long userId, Long questionId) {
        CommunityQuestion question = getById(questionId);
        log.info("question: " + question);
        log.info("userId: " + userId);
        log.info("question.getUserId(): " + question.getUserId());
        return question != null && question.getUserId().equals(userId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markQuestionSolved(Long questionId, Long answerId) {
        CommunityQuestion question = getById(questionId);
        if (question == null) {
            return false;
        }
        
        // 更新问题状态
        question.setBestAnswerId(answerId);
        question.setStatus("SOLVED");
        question.setUpdatedAt(LocalDateTime.now());
        
        boolean result = updateById(question);
        
        // 清除相关缓存
        if (result) {
            cacheService.delete(CacheConstants.QUESTION_DETAIL_CACHE_KEY_PREFIX + questionId);
            cacheService.delete(CacheConstants.UNSOLVED_QUESTIONS_CACHE_KEY);
        }
        
        return result;
    }
    
    @Override
    public Page<QuestionVO> getRelatedQuestions(Long questionId, Integer limit, Long userId) {
        // 尝试从缓存获取
        String cacheKey = CacheConstants.RELATED_QUESTIONS_CACHE_KEY_PREFIX + questionId + ":" + limit;
        Page<QuestionVO> cachedPage = cacheService.get(cacheKey, Page.class);
        if (cachedPage != null) {
            log.debug("从缓存获取相关问题列表: {}", questionId);
            // 更新用户交互状态
            if (userId != null) {
                updateUserInteractionStatus(cachedPage.getRecords(), userId);
            }
            return cachedPage;
        }
        
        // 创建分页对象
        Page<CommunityQuestion> pageParam = new Page<>(1, limit);
        
        // 获取当前问题
        CommunityQuestion currentQuestion = getById(questionId);
        if (currentQuestion == null) {
            return new Page<>(1, limit);
        }
        
        // 根据标题相似度查询相关问题
        // 这里简化实现，实际可能需要更复杂的相关性算法
        LambdaQueryWrapper<CommunityQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(CommunityQuestion::getId, questionId) // 排除当前问题
                   .like(CommunityQuestion::getTitle, currentQuestion.getTitle())
                   .orderByDesc(CommunityQuestion::getViewCount);
        
        // 执行分页查询
        Page<CommunityQuestion> questionPage = page(pageParam, queryWrapper);
        
        // 转换为VO
        Page<QuestionVO> result = convertToQuestionVOPage(questionPage, userId);
        
        // 缓存相关问题
        Page<QuestionVO> cacheData = convertToQuestionVOPage(questionPage, null);
        cacheService.set(cacheKey, cacheData, CacheConstants.QUESTION_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return result;
    }
    
    @Override
    public void incrementAnswerCount(Long questionId) {
        questionMapper.incrementAnswerCount(questionId);
    }
    
    @Override
    public void decrementAnswerCount(Long questionId) {
        questionMapper.decrementAnswerCount(questionId);
    }
    
    /**
     * 保存问题标签关联
     * @param questionId 问题ID
     * @param tagIds 标签ID列表
     */
    private void saveQuestionTags(Long questionId, List<Long> tagIds) {
        if (CollectionUtils.isEmpty(tagIds)) {
            return;
        }
        
        // 使用tagService的addTagsToContent方法添加标签关联，同时更新标签使用次数
        tagService.addTagsToContent("QUESTION", questionId, tagIds);
    }
    
    /**
     * 转换问题实体为问题VO
     * @param question 问题实体
     * @param userId 当前用户ID
     * @return 问题VO
     */
    private QuestionVO convertToQuestionVO(CommunityQuestion question, Long userId) {
        if (question == null) {
            return null;
        }
        
        QuestionVO questionVO = new QuestionVO();
        questionVO.setId(question.getId());
        questionVO.setTitle(question.getTitle());
        questionVO.setContent(question.getContent());
        questionVO.setUserId(question.getUserId());
        
        // TODO: 获取用户信息，需要用户服务
        questionVO.setUserName("用户" + question.getUserId());
        questionVO.setUserAvatar("/avatars/default/avatar.jpg");
        
        questionVO.setViewCount(question.getViewCount());
        questionVO.setAnswerCount(question.getAnswerCount());
        questionVO.setStatus(question.getStatus());
        questionVO.setBestAnswerId(question.getBestAnswerId());
        questionVO.setCreatedAt(question.getCreatedAt());
        questionVO.setUpdatedAt(question.getUpdatedAt());
        
        // 获取标签列表
        List<TagVO> tagList = tagService.getQuestionTags(question.getId());
        questionVO.setTags(tagList != null ? tagList : new ArrayList<>());
        
        // 设置当前用户是否已收藏
        if (userId != null) {
            questionVO.setFavorited(checkUserFavoritedQuestion(userId, question.getId()));
        } else {
            questionVO.setFavorited(false);
        }
        
        return questionVO;
    }
    
    /**
     * 转换问题分页为问题VO分页
     * @param questionPage 问题分页
     * @param userId 当前用户ID
     * @return 问题VO分页
     */
    private Page<QuestionVO> convertToQuestionVOPage(Page<CommunityQuestion> questionPage, Long userId) {
        Page<QuestionVO> voPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        
        List<QuestionVO> voList = questionPage.getRecords().stream()
                .map(question -> convertToQuestionVO(question, userId))
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    /**
     * 更新问题VO列表中的用户交互状态
     * @param questionVOs 问题VO列表
     * @param userId 当前用户ID
     */
    private void updateUserInteractionStatus(List<QuestionVO> questionVOs, Long userId) {
        if (questionVOs == null || userId == null) {
            return;
        }
        
        for (QuestionVO questionVO : questionVOs) {
            questionVO.setFavorited(checkUserFavoritedQuestion(userId, questionVO.getId()));
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
     * 获取回答作者ID
     * @param answerId 回答ID
     * @return 作者ID
     */
    private Long getAnswerAuthorId(Long answerId) {
        // 通过answerMapper查询回答作者ID
        return answerMapper.selectAnswerAuthorId(answerId);
    }
} 