package com.csu.unicorp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.csu.unicorp.dto.community.AnswerDTO;
import com.csu.unicorp.entity.community.CommunityAnswer;
import com.csu.unicorp.vo.community.AnswerVO;

/**
 * 社区问题回答Service接口
 */
public interface CommunityAnswerService extends IService<CommunityAnswer> {
    
    /**
     * 创建回答
     * @param userId 用户ID
     * @param answerDTO 回答DTO
     * @return 回答ID
     */
    Long createAnswer(Long userId, AnswerDTO answerDTO);
    
    /**
     * 获取回答详情
     * @param answerId 回答ID
     * @param userId 当前用户ID（可选）
     * @return 回答详情
     */
    AnswerVO getAnswerDetail(Long answerId, Long userId);
    
    /**
     * 更新回答
     * @param userId 用户ID
     * @param answerId 回答ID
     * @param answerDTO 回答DTO
     * @return 是否成功
     */
    boolean updateAnswer(Long userId, Long answerId, AnswerDTO answerDTO);
    
    /**
     * 删除回答
     * @param userId 用户ID
     * @param answerId 回答ID
     * @return 是否成功
     */
    boolean deleteAnswer(Long userId, Long answerId);
    
    /**
     * 检查用户是否有权限编辑回答
     * @param userId 用户ID
     * @param answerId 回答ID
     * @return 是否有权限
     */
    boolean checkAnswerEditPermission(Long userId, Long answerId);
    
    /**
     * 获取问题的回答列表
     * @param questionId 问题ID
     * @param page 页码
     * @param size 每页大小
     * @param userId 当前用户ID（可选）
     * @return 回答列表
     */
    Page<AnswerVO> getAnswersByQuestion(Long questionId, Integer page, Integer size, Long userId);
    
    /**
     * 获取用户的回答列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @param currentUserId 当前用户ID（可选）
     * @return 回答列表
     */
    Page<AnswerVO> getUserAnswers(Long userId, Integer page, Integer size, Long currentUserId);
    
    /**
     * 标记回答为已接受
     * @param answerId 回答ID
     * @return 是否成功
     */
    boolean markAsAccepted(Long answerId);
} 