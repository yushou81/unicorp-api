package com.csu.unicorp.mapper.community;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.community.CommunityAnswer;

/**
 * 社区回答Mapper接口
 */
public interface CommunityAnswerMapper extends BaseMapper<CommunityAnswer> {
    
    /**
     * 获取问题的回答列表
     * @param page 分页参数
     * @param questionId 问题ID
     * @return 回答列表
     */
    @Select("SELECT a.* FROM community_answer a " +
            "WHERE a.question_id = #{questionId} " +
            "ORDER BY a.is_accepted DESC, a.like_count DESC, a.created_at DESC")
    Page<CommunityAnswer> selectAnswersByQuestionId(Page<CommunityAnswer> page, @Param("questionId") Long questionId);
    
    /**
     * 获取用户的回答列表
     * @param page 分页参数
     * @param userId 用户ID
     * @return 回答列表
     */
    @Select("SELECT a.* FROM community_answer a " +
            "WHERE a.user_id = #{userId} " +
            "ORDER BY a.created_at DESC")
    Page<CommunityAnswer> selectAnswersByUserId(Page<CommunityAnswer> page, @Param("userId") Long userId);
    
    /**
     * 增加回答点赞数量
     * @param answerId 回答ID
     * @return 影响行数
     */
    @Update("UPDATE community_answer SET like_count = like_count + 1 WHERE id = #{answerId}")
    int incrementLikeCount(@Param("answerId") Long answerId);
    
    /**
     * 减少回答点赞数量
     * @param answerId 回答ID
     * @return 影响行数
     */
    @Update("UPDATE community_answer SET like_count = GREATEST(0, like_count - 1) WHERE id = #{answerId}")
    int decrementLikeCount(@Param("answerId") Long answerId);
    
    /**
     * 设置回答采纳状态
     * @param answerId 回答ID
     * @param isAccepted 是否采纳
     * @return 影响行数
     */
    @Update("UPDATE community_answer SET is_accepted = #{isAccepted} WHERE id = #{answerId}")
    int updateAcceptedStatus(@Param("answerId") Long answerId, @Param("isAccepted") Boolean isAccepted);
    
    /**
     * 清除问题下所有回答的采纳状态
     * @param questionId 问题ID
     * @return 影响行数
     */
    @Update("UPDATE community_answer SET is_accepted = 0 WHERE question_id = #{questionId}")
    int clearAcceptedStatus(@Param("questionId") Long questionId);
    
    /**
     * 获取问题的最佳回答
     * @param questionId 问题ID
     * @return 最佳回答
     */
    @Select("SELECT a.* FROM community_answer a " +
            "WHERE a.question_id = #{questionId} AND a.is_accepted = 1 " +
            "LIMIT 1")
    CommunityAnswer selectBestAnswerByQuestionId(@Param("questionId") Long questionId);
    
    /**
     * 获取回答作者ID
     * @param answerId 回答ID
     * @return 作者ID
     */
    @Select("SELECT user_id FROM community_answer WHERE id = #{answerId}")
    Long selectAnswerAuthorId(@Param("answerId") Long answerId);
} 