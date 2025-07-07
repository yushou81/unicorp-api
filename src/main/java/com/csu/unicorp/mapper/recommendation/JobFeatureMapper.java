package com.csu.unicorp.mapper.recommendation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.recommendation.JobFeature;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 岗位特征Mapper接口
 */
@Mapper
public interface JobFeatureMapper extends BaseMapper<JobFeature> {
    
    /**
     * 根据岗位ID获取岗位特征
     *
     * @param jobId 岗位ID
     * @return 岗位特征
     */
    @Select("SELECT * FROM job_features WHERE job_id = #{jobId}")
    JobFeature selectByJobId(@Param("jobId") Integer jobId);
    
    /**
     * 获取所有开放状态岗位的特征
     *
     * @return 岗位特征列表
     */
    @Select("SELECT jf.* FROM job_features jf " +
            "JOIN jobs j ON jf.job_id = j.id " +
            "WHERE j.status = 'open' AND j.is_deleted = 0")
    List<JobFeature> selectAllActiveJobFeatures();
    
    /**
     * 获取具有特定技能要求的岗位特征
     *
     * @param skill 技能关键词
     * @return 岗位特征列表
     */
    @Select("SELECT jf.* FROM job_features jf " +
            "JOIN jobs j ON jf.job_id = j.id " +
            "WHERE jf.required_skills LIKE CONCAT('%', #{skill}, '%') " +
            "AND j.status = 'open' AND j.is_deleted = 0")
    List<JobFeature> selectByRequiredSkill(@Param("skill") String skill);
    
    /**
     * 获取特定分类的岗位特征
     *
     * @param categoryId 分类ID
     * @return 岗位特征列表
     */
    @Select("SELECT jf.* FROM job_features jf " +
            "JOIN jobs j ON jf.job_id = j.id " +
            "JOIN job_category_relations jcr ON j.id = jcr.job_id " +
            "WHERE jcr.category_id = #{categoryId} " +
            "AND j.status = 'open' AND j.is_deleted = 0")
    List<JobFeature> selectByCategoryId(@Param("categoryId") Integer categoryId);
} 