package com.csu.unicorp.mapper.recommendation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.recommendation.TalentRecommendation;
import com.csu.unicorp.vo.StudentTalentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 人才推荐Mapper接口
 */
@Mapper
public interface TalentRecommendationMapper extends BaseMapper<TalentRecommendation> {
    
    /**
     * 分页获取企业的人才推荐列表，包含学生详情
     *
     * @param page 分页参数
     * @param organizationId 组织ID
     * @return 人才推荐列表
     */
    @Select("SELECT tr.*, u.id as user_id, u.account, u.nickname, u.email, u.phone, " +
            "s.major, s.education_level, s.graduation_year " +
            "FROM talent_recommendations tr " +
            "JOIN users u ON tr.student_id = u.id " +
            "JOIN student_profiles s ON u.id = s.user_id " +
            "WHERE tr.organization_id = #{organizationId} " +
            "ORDER BY tr.score DESC")
    IPage<StudentTalentVO> pageRecommendedTalentsWithDetails(Page<StudentTalentVO> page, @Param("organizationId") Integer organizationId);
    
    /**
     * 更新推荐状态
     *
     * @param id 推荐ID
     * @param status 新状态
     * @return 影响行数
     */
    @Update("UPDATE talent_recommendations SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateRecommendationStatus(@Param("id") Integer id, @Param("status") String status);
    
    /**
     * 检查特定学生是否已经被推荐给企业
     *
     * @param organizationId 组织ID
     * @param studentId 学生ID
     * @return 推荐数量
     */
    @Select("SELECT COUNT(*) FROM talent_recommendations WHERE organization_id = #{organizationId} AND student_id = #{studentId}")
    int countExistingRecommendation(@Param("organizationId") Integer organizationId, @Param("studentId") Integer studentId);
} 