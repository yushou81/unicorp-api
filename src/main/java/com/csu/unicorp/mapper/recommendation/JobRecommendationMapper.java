package com.csu.unicorp.mapper.recommendation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.recommendation.JobRecommendation;
import com.csu.unicorp.vo.JobRecommendationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 岗位推荐Mapper接口
 */
@Mapper
public interface JobRecommendationMapper extends BaseMapper<JobRecommendation> {
    
    /**
     * 分页获取用户的岗位推荐列表，包含岗位详情
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @return 岗位推荐列表
     */
    @Select("SELECT jr.id, jr.job_id as jobId, jr.score, jr.reason, jr.status, jr.created_at as createdAt, " +
            "j.title, j.location, j.job_type as jobType, j.salary_min as salaryMin, j.salary_max as salaryMax, " +
            "j.salary_unit as salaryUnit, j.tags, " +
            "o.organization_name as organizationName, " +
            "(SELECT jcr.category_id FROM job_category_relations jcr WHERE jcr.job_id = j.id LIMIT 1) as categoryId " +
            "FROM job_recommendations jr " +
            "JOIN jobs j ON jr.job_id = j.id " +
            "JOIN organizations o ON j.organization_id = o.id " +
            "WHERE jr.user_id = #{userId} AND j.is_deleted = 0 AND j.status = 'open' " +
            "ORDER BY jr.score DESC")
    IPage<JobRecommendationVO> pageRecommendedJobsWithDetails(Page<JobRecommendationVO> page, @Param("userId") Integer userId);
    
    /**
     * 更新推荐状态
     *
     * @param id 推荐ID
     * @param status 新状态
     * @return 影响行数
     */
    @Update("UPDATE job_recommendations SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateRecommendationStatus(@Param("id") Integer id, @Param("status") String status);
    
    /**
     * 检查特定岗位是否已经被推荐给用户
     *
     * @param userId 用户ID
     * @param jobId 岗位ID
     * @return 推荐数量
     */
    @Select("SELECT COUNT(*) FROM job_recommendations WHERE user_id = #{userId} AND job_id = #{jobId}")
    int countExistingRecommendation(@Param("userId") Integer userId, @Param("jobId") Integer jobId);
} 