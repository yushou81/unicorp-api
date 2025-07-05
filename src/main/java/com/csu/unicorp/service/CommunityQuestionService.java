package com.csu.unicorp.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.csu.unicorp.dto.community.QuestionDTO;
import com.csu.unicorp.entity.community.CommunityQuestion;
import com.csu.unicorp.vo.community.QuestionVO;

/**
 * 社区问题Service接口
 */
public interface CommunityQuestionService extends IService<CommunityQuestion> {
    
    /**
     * 创建问题
     * @param userId 用户ID
     * @param questionDTO 问题DTO
     * @return 问题ID
     */
    Long createQuestion(Long userId, QuestionDTO questionDTO);
    
    /**
     * 更新问题
     * @param userId 用户ID
     * @param questionId 问题ID
     * @param questionDTO 问题DTO
     * @return 是否成功
     */
    boolean updateQuestion(Long userId, Long questionId, QuestionDTO questionDTO);
    
    /**
     * 删除问题
     * @param userId 用户ID
     * @param questionId 问题ID
     * @return 是否成功
     */
    boolean deleteQuestion(Long userId, Long questionId);
    
    /**
     * 获取问题详情
     * @param questionId 问题ID
     * @param userId 当前用户ID（可选）
     * @return 问题详情
     */
    QuestionVO getQuestionDetail(Long questionId, Long userId);
    
    /**
     * 获取分类问题列表
     * @param categoryId 分类ID
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID（可选）
     * @return 问题列表
     */
    Page<QuestionVO> getQuestionsByCategory(Long categoryId, int page, int size, Long userId);
    
    /**
     * 获取热门问题列表
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID（可选）
     * @return 热门问题列表
     */
    Page<QuestionVO> getHotQuestions(int page, int size, Long userId);
    
    /**
     * 获取最新问题列表
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID（可选）
     * @return 最新问题列表
     */
    Page<QuestionVO> getLatestQuestions(int page, int size, Long userId);
    
    /**
     * 获取悬赏问题列表
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID（可选）
     * @return 悬赏问题列表
     */
    Page<QuestionVO> getBountyQuestions(int page, int size, Long userId);
    
    /**
     * 获取用户问题列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param currentUserId 当前用户ID（可选）
     * @return 用户问题列表
     */
    Page<QuestionVO> getUserQuestions(Long userId, int page, int size, Long currentUserId);
    
    /**
     * 设置问题最佳答案
     * @param userId 用户ID
     * @param questionId 问题ID
     * @param answerId 答案ID
     * @return 是否成功
     */
    boolean setBestAnswer(Long userId, Long questionId, Long answerId);
    
    /**
     * 关闭问题
     * @param userId 用户ID
     * @param questionId 问题ID
     * @return 是否成功
     */
    boolean closeQuestion(Long userId, Long questionId);
    
    /**
     * 增加问题浏览次数
     * @param questionId 问题ID
     */
    void incrementViewCount(Long questionId);
    
    /**
     * 搜索问题
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID（可选）
     * @return 问题列表
     */
    Page<QuestionVO> searchQuestions(String keyword, int page, int size, Long userId);
    
    /**
     * 获取推荐问题列表
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 推荐问题列表
     */
    List<QuestionVO> getRecommendQuestions(Long userId, int limit);
    
    /**
     * 检查用户是否有权限编辑问题
     * @param userId 用户ID
     * @param questionId 问题ID
     * @return 是否有权限
     */
    boolean checkQuestionEditPermission(Long userId, Long questionId);
    
    /**
     * 收藏问题
     * @param userId 用户ID
     * @param questionId 问题ID
     * @return 是否成功
     */
    boolean favoriteQuestion(Long userId, Long questionId);
    
    /**
     * 取消收藏问题
     * @param userId 用户ID
     * @param questionId 问题ID
     * @return 是否成功
     */
    boolean unfavoriteQuestion(Long userId, Long questionId);
    
    /**
     * 检查用户是否已收藏问题
     * @param userId 用户ID
     * @param questionId 问题ID
     * @return 是否已收藏
     */
    boolean checkUserFavoritedQuestion(Long userId, Long questionId);
    
    /**
     * 检查问题是否存在
     * @param questionId 问题ID
     * @return 是否存在
     */
    boolean existsById(Long questionId);
    
    /**
     * 获取未解决问题列表
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID（可选）
     * @return 未解决问题列表
     */
    Page<QuestionVO> getUnsolvedQuestions(Integer page, Integer size, Long userId);
    
    /**
     * 判断用户是否为问题作者
     * @param userId 用户ID
     * @param questionId 问题ID
     * @return 是否为问题作者
     */
    boolean isQuestionAuthor(Long userId, Long questionId);
    
    /**
     * 标记问题为已解决
     * @param questionId 问题ID
     * @param answerId 被采纳的答案ID
     * @return 是否成功
     */
    boolean markQuestionSolved(Long questionId, Long answerId);
    
    /**
     * 获取相关问题列表
     * @param questionId 问题ID
     * @param limit 限制数量
     * @param userId 当前用户ID（可选）
     * @return 相关问题列表
     */
    Page<QuestionVO> getRelatedQuestions(Long questionId, Integer limit, Long userId);
    
    /**
     * 增加问题回答数量
     * @param questionId 问题ID
     */
    void incrementAnswerCount(Long questionId);
    
    /**
     * 减少问题回答数量
     * @param questionId 问题ID
     */
    void decrementAnswerCount(Long questionId);
} 