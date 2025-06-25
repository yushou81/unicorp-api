package com.csu.linkneiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.linkneiapi.entity.JobPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 招聘岗位数据访问接口
 */
@Mapper
public interface JobPostMapper extends BaseMapper<JobPost> {
    
    /**
     * 根据企业ID查询招聘岗位
     * @param enterpriseId 企业ID
     * @return 岗位列表
     */
    @Select("SELECT * FROM job_post WHERE enterprise_id = #{enterpriseId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<JobPost> findByEnterpriseId(@Param("enterpriseId") Long enterpriseId);
    
    /**
     * 分页查询招聘岗位（带条件筛选）
     * @param page 分页对象
     * @param jobType 工作类型
     * @param keyword 关键词
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM job_post WHERE is_deleted = 0 " +
            "<if test=\"jobType != null and jobType != ''\">AND job_type = #{jobType} </if>" +
            "<if test=\"keyword != null and keyword != ''\">AND (title LIKE CONCAT('%',#{keyword},'%') OR responsibilities LIKE CONCAT('%',#{keyword},'%') OR requirements LIKE CONCAT('%',#{keyword},'%')) </if>" +
            "AND status = 'HIRING' " +
            "ORDER BY create_time DESC" +
            "</script>")
    IPage<JobPost> pageJobs(Page<JobPost> page, @Param("jobType") String jobType, @Param("keyword") String keyword);
} 