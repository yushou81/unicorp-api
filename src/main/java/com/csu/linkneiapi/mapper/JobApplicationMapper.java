package com.csu.linkneiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.linkneiapi.entity.JobApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 投递记录数据访问接口
 */
@Mapper
public interface JobApplicationMapper extends BaseMapper<JobApplication> {
    
    /**
     * 根据用户ID查询投递记录
     * @param userId 用户ID
     * @return 投递记录列表
     */
    @Select("SELECT * FROM job_application WHERE user_id = #{userId} ORDER BY application_time DESC")
    List<JobApplication> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据岗位ID查询投递记录
     * @param jobPostId 岗位ID
     * @return 投递记录列表
     */
    @Select("SELECT * FROM job_application WHERE job_post_id = #{jobPostId} ORDER BY application_time DESC")
    List<JobApplication> findByJobPostId(@Param("jobPostId") Long jobPostId);
    
    /**
     * 判断用户是否已投递该岗位
     * @param userId 用户ID
     * @param jobPostId 岗位ID
     * @return 投递记录
     */
    @Select("SELECT * FROM job_application WHERE user_id = #{userId} AND job_post_id = #{jobPostId} LIMIT 1")
    JobApplication findByUserIdAndJobPostId(@Param("userId") Long userId, @Param("jobPostId") Long jobPostId);
    
    /**
     * 分页查询企业收到的投递记录
     * @param page 分页对象
     * @param enterpriseId 企业ID
     * @param status 投递状态
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT a.* FROM job_application a " +
            "JOIN job_post p ON a.job_post_id = p.id " +
            "WHERE p.enterprise_id = #{enterpriseId} " +
            "<if test=\"status != null and status != ''\">AND a.status = #{status} </if>" +
            "ORDER BY a.application_time DESC" +
            "</script>")
    IPage<JobApplication> pageByEnterpriseId(Page<JobApplication> page, @Param("enterpriseId") Long enterpriseId, @Param("status") String status);
} 