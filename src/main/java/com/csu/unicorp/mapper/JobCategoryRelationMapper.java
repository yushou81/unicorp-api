package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.JobCategoryRelation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 岗位与分类关联Mapper接口
 */
@Mapper
public interface JobCategoryRelationMapper extends BaseMapper<JobCategoryRelation> {

    /**
     * 根据岗位ID获取关联的分类ID列表
     *
     * @param jobId 岗位ID
     * @return 分类ID列表
     */
    @Select("SELECT category_id FROM job_category_relations WHERE job_id = #{jobId}")
    List<Integer> selectCategoryIdsByJobId(@Param("jobId") Integer jobId);

    /**
     * 根据岗位ID删除所有关联
     *
     * @param jobId 岗位ID
     * @return 影响行数
     */
    @Delete("DELETE FROM job_category_relations WHERE job_id = #{jobId}")
    int deleteByJobId(@Param("jobId") Integer jobId);

    /**
     * 根据分类ID获取关联的岗位ID列表
     *
     * @param categoryId 分类ID
     * @return 岗位ID列表
     */
    @Select("SELECT job_id FROM job_category_relations WHERE category_id = #{categoryId}")
    List<Integer> selectJobIdsByCategoryId(@Param("categoryId") Integer categoryId);
} 