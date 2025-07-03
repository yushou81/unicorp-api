package com.csu.unicorp.mapper.achievement;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.achievement.AchievementView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 成果访问记录Mapper接口
 */
@Mapper
public interface AchievementViewMapper extends BaseMapper<AchievementView> {
    
    /**
     * 根据成果类型和ID查询访问记录
     * 
     * @param achievementType 成果类型
     * @param achievementId 成果ID
     * @return 访问记录列表
     */
    @Select("SELECT * FROM achievement_views WHERE achievement_type = #{achievementType} AND achievement_id = #{achievementId}")
    List<AchievementView> selectByAchievementTypeAndId(@Param("achievementType") String achievementType, @Param("achievementId") Integer achievementId);
    
    /**
     * 统计成果的访问次数
     * 
     * @param achievementType 成果类型
     * @param achievementId 成果ID
     * @return 访问次数
     */
    @Select("SELECT COUNT(*) FROM achievement_views WHERE achievement_type = #{achievementType} AND achievement_id = #{achievementId}")
    int countByAchievementTypeAndId(@Param("achievementType") String achievementType, @Param("achievementId") Integer achievementId);
    
    /**
     * 统计用户成果的总访问次数
     * 
     * @param userId 用户ID
     * @return 访问次数
     */
    @Select("SELECT (" +
            "(SELECT COUNT(*) FROM achievement_views av " +
            "JOIN portfolio_items pi ON av.achievement_type = 'portfolio' AND av.achievement_id = pi.id AND pi.user_id = #{userId}) + " +
            "(SELECT COUNT(*) FROM achievement_views av " +
            "JOIN competition_awards ca ON av.achievement_type = 'award' AND av.achievement_id = ca.id AND ca.user_id = #{userId}) + " +
            "(SELECT COUNT(*) FROM achievement_views av " +
            "JOIN research_achievements ra ON av.achievement_type = 'research' AND av.achievement_id = ra.id AND ra.user_id = #{userId})" +
            ") AS total_count")
    int countByUserId(@Param("userId") Integer userId);
    
    /**
     * 统计用户各类成果的访问次数
     * 
     * @param userId 用户ID
     * @return 各类成果的访问次数
     */
    @Select("SELECT 'portfolio' as type, COUNT(*) as count FROM achievement_views av " +
            "JOIN portfolio_items pi ON av.achievement_type = 'portfolio' AND av.achievement_id = pi.id AND pi.user_id = #{userId} " +
            "UNION ALL " +
            "SELECT 'award' as type, COUNT(*) as count FROM achievement_views av " +
            "JOIN competition_awards ca ON av.achievement_type = 'award' AND av.achievement_id = ca.id AND ca.user_id = #{userId} " +
            "UNION ALL " +
            "SELECT 'research' as type, COUNT(*) as count FROM achievement_views av " +
            "JOIN research_achievements ra ON av.achievement_type = 'research' AND av.achievement_id = ra.id AND ra.user_id = #{userId}")
    List<Map<String, Object>> countByUserIdGroupByType(@Param("userId") Integer userId);
} 