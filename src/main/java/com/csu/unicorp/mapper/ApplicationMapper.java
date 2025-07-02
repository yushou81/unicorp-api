package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.Application;
import com.csu.unicorp.vo.ApplicationDetailVO;
import com.csu.unicorp.vo.MyApplicationDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 岗位申请Mapper接口
 */
@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {
    
    /**
     * 查询岗位的申请列表
     *
     * @param page  分页参数
     * @param jobId 岗位ID
     * @return 申请列表
     */
    @Select({
            "SELECT a.id, a.job_id, a.student_id, a.resume_id, a.status, a.applied_at, ",
            "u.nickname, v.real_name, r.major, r.education_level, r.resume_url ",
            "FROM applications a ",
            "LEFT JOIN users u ON a.student_id = u.id ",
            "LEFT JOIN user_verifications v ON a.student_id = v.user_id ",
            "LEFT JOIN resumes r ON a.resume_id = r.id ",
            "WHERE a.job_id = #{jobId} ",
            "ORDER BY a.applied_at DESC"
    })
    IPage<ApplicationDetailVO> pageApplicationsByJobId(Page<ApplicationDetailVO> page, @Param("jobId") Integer jobId);
    
    /**
     * 查询学生的申请列表
     *
     * @param page      分页参数
     * @param studentId 学生ID
     * @return 申请列表
     */
    @Select({
            "SELECT a.id as application_id, a.resume_id, a.status, a.applied_at, ",
            "j.id as 'jobInfo.jobId', j.title as 'jobInfo.jobTitle', o.organization_name as 'jobInfo.organizationName' ",
            "FROM applications a ",
            "LEFT JOIN jobs j ON a.job_id = j.id ",
            "LEFT JOIN organizations o ON j.organization_id = o.id ",
            "WHERE a.student_id = #{studentId} ",
            "ORDER BY a.applied_at DESC"
    })
    IPage<MyApplicationDetailVO> pageApplicationsByStudentId(Page<MyApplicationDetailVO> page, @Param("studentId") Integer studentId);
    
    /**
     * 检查学生是否已申请过该岗位
     *
     * @param jobId     岗位ID
     * @param studentId 学生ID
     * @return 申请记录数
     */
    @Select("SELECT COUNT(*) FROM applications WHERE job_id = #{jobId} AND student_id = #{studentId}")
    int countApplicationByJobIdAndStudentId(@Param("jobId") Integer jobId, @Param("studentId") Integer studentId);
    
    /**
     * 根据ID查询申请详情
     *
     * @param id 申请ID
     * @return 申请详情
     */
    @Select({
            "SELECT a.id, a.job_id, a.student_id, a.resume_id, a.status, a.applied_at, ",
            "u.nickname, v.real_name, r.major, r.education_level, r.resume_url ",
            "FROM applications a ",
            "LEFT JOIN users u ON a.student_id = u.id ",
            "LEFT JOIN user_verifications v ON a.student_id = v.user_id ",
            "LEFT JOIN resumes r ON a.resume_id = r.id ",
            "WHERE a.id = #{id}"
    })
    ApplicationDetailVO getApplicationDetailById(@Param("id") Integer id);
} 