package com.csu.unicorp.mapper.community;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.community.CommunityQuestion;

/**
 * 社区问题Mapper接口
 */
@Repository
public interface CommunityQuestionMapper extends BaseMapper<CommunityQuestion> {
    
    /**
     * 增加问题浏览次数
     * @param questionId 问题ID
     * @return 影响行数
     */
    @Update("UPDATE community_question SET view_count = view_count + 1 WHERE id = #{questionId}")
    int incrementViewCount(@Param("questionId") Long questionId);
    
    /**
     * 增加问题回答数量
     * @param questionId 问题ID
     * @return 影响行数
     */
    @Update("UPDATE community_question SET answer_count = answer_count + 1 WHERE id = #{questionId}")
    int incrementAnswerCount(@Param("questionId") Long questionId);
    
    /**
     * 减少问题回答数量
     * @param questionId 问题ID
     * @return 影响行数
     */
    @Update("UPDATE community_question SET answer_count = GREATEST(0, answer_count - 1) WHERE id = #{questionId}")
    int decrementAnswerCount(@Param("questionId") Long questionId);
    
    /**
     * 获取热门问题列表
     * @param page 分页参数
     * @return 热门问题列表
     */
    @Select("SELECT * FROM community_question ORDER BY view_count DESC, answer_count DESC, created_at DESC")
    Page<CommunityQuestion> selectHotQuestions(Page<CommunityQuestion> page);
    
    /**
     * 获取最新问题列表
     * @param page 分页参数
     * @return 最新问题列表
     */
    @Select("SELECT * FROM community_question ORDER BY created_at DESC")
    Page<CommunityQuestion> selectLatestQuestions(Page<CommunityQuestion> page);
    
    /**
     * 获取用户的问题列表
     * @param page 分页参数
     * @param userId 用户ID
     * @return 用户问题列表
     */
    @Select("SELECT * FROM community_question WHERE user_id = #{userId} ORDER BY created_at DESC")
    Page<CommunityQuestion> selectQuestionsByUserId(Page<CommunityQuestion> page, @Param("userId") Long userId);
    
    /**
     * 获取未解决的问题列表
     * @param page 分页参数
     * @return 未解决问题列表
     */
    @Select("SELECT * FROM community_question WHERE status = 'UNSOLVED' ORDER BY created_at DESC")
    Page<CommunityQuestion> selectUnsolvedQuestions(Page<CommunityQuestion> page);
} 