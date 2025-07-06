package com.csu.unicorp.mapper.recommendation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.recommendation.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 用户行为Mapper接口
 */
@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {
    
    /**
     * 获取用户对特定目标的行为列表
     *
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 用户行为列表
     */
    @Select("SELECT * FROM user_behaviors WHERE user_id = #{userId} AND target_type = #{targetType} AND target_id = #{targetId}")
    List<UserBehavior> selectUserBehaviorsForTarget(@Param("userId") Integer userId, 
                                                  @Param("targetType") String targetType, 
                                                  @Param("targetId") Integer targetId);
    
    /**
     * 获取用户的所有行为
     *
     * @param userId 用户ID
     * @return 用户行为列表
     */
    @Select("SELECT * FROM user_behaviors WHERE user_id = #{userId} ORDER BY occurred_at DESC")
    List<UserBehavior> selectUserBehaviors(@Param("userId") Integer userId);
    
    /**
     * 获取用户的特定类型行为
     *
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @return 用户行为列表
     */
    @Select("SELECT * FROM user_behaviors WHERE user_id = #{userId} AND behavior_type = #{behaviorType} ORDER BY occurred_at DESC")
    List<UserBehavior> selectUserBehaviorsByType(@Param("userId") Integer userId, @Param("behaviorType") String behaviorType);
    
    /**
     * 统计用户对各个岗位分类的兴趣度
     *
     * @param userId 用户ID
     * @return 分类ID和兴趣度的映射
     */
    @Select("SELECT jcr.category_id, SUM(ub.weight) as interest_score " +
            "FROM user_behaviors ub " +
            "JOIN jobs j ON ub.target_id = j.id AND ub.target_type = 'job' " +
            "JOIN job_category_relations jcr ON j.id = jcr.job_id " +
            "WHERE ub.user_id = #{userId} " +
            "GROUP BY jcr.category_id " +
            "ORDER BY interest_score DESC")
    List<Map<String, Object>> calculateUserCategoryInterests(@Param("userId") Integer userId);
    
    /**
     * 获取用户最近的搜索关键词
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 搜索关键词列表
     */
    @Select("SELECT search_keyword FROM user_behaviors " +
            "WHERE user_id = #{userId} AND behavior_type = 'search' AND search_keyword IS NOT NULL " +
            "ORDER BY occurred_at DESC LIMIT #{limit}")
    List<String> getRecentSearchKeywords(@Param("userId") Integer userId, @Param("limit") int limit);
} 