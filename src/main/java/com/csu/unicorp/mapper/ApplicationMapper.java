package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.Application;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 岗位申请Mapper接口
 */
@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {
    
    /**
     * 查询某个岗位的所有申请，包含学生资料
     * 
     * @param page 分页参数
     * @param jobId 岗位ID
     * @return 分页结果
     */
    @Select("SELECT a.*, u.nickname, uv.real_name, sp.major, sp.education_level, sp.resume_url " +
            "FROM applications a " +
            "LEFT JOIN users u ON a.student_id = u.id " +
            "LEFT JOIN user_verifications uv ON a.student_id = uv.user_id " +
            "LEFT JOIN student_profiles sp ON a.student_id = sp.user_id " +
            "WHERE a.job_id = #{jobId} " +
            "ORDER BY a.applied_at DESC")
    IPage<Application> selectApplicationsWithStudentInfo(Page<Application> page, @Param("jobId") Integer jobId);
    
    /**
     * 检查学生是否已申请某个岗位
     * 
     * @param jobId 岗位ID
     * @param studentId 学生ID
     * @return 申请记录数量
     */
    @Select("SELECT COUNT(*) FROM applications WHERE job_id = #{jobId} AND student_id = #{studentId}")
    Integer countStudentApplication(@Param("jobId") Integer jobId, @Param("studentId") Integer studentId);
} 