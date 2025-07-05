package com.csu.unicorp.mapper.course;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.course.CourseEnrollment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 课程报名Mapper接口
 */
@Mapper
public interface CourseEnrollmentMapper extends BaseMapper<CourseEnrollment> {

    /**
     * 根据课程ID查询报名学生
     * 
     * @param courseId 课程ID
     * @param page 分页参数
     * @return 报名的学生列表
     */
    @Select("SELECT ce.* FROM course_enrollments ce WHERE ce.course_id = #{courseId} AND ce.is_deleted = 0")
    IPage<CourseEnrollment> selectEnrollmentsByCourseId(@Param("courseId") Integer courseId, Page<CourseEnrollment> page);
    
    /**
     * 根据学生ID查询报名的课程（不包括已取消的报名）
     * 
     * @param studentId 学生ID
     * @param page 分页参数
     * @return 学生报名的课程列表
     */
    @Select("SELECT ce.* FROM course_enrollments ce WHERE ce.student_id = #{studentId} AND ce.status != 'cancelled' AND ce.is_deleted = 0")
    IPage<CourseEnrollment> selectEnrollmentsByStudentId(@Param("studentId") Integer studentId, Page<CourseEnrollment> page);
    
    /**
     * 查询课程的报名人数
     * 
     * @param courseId 课程ID
     * @return 报名人数
     */
    @Select("SELECT COUNT(*) FROM course_enrollments WHERE course_id = #{courseId} AND status = 'enrolled' AND is_deleted = 0")
    Integer countEnrollmentsByCourseId(@Param("courseId") Integer courseId);
    
    /**
     * 查询课程的已注册学生数量
     * 
     * @param courseId 课程ID
     * @return 已注册学生数量
     */
    @Select("SELECT COUNT(*) FROM course_enrollments WHERE course_id = #{courseId} AND status = 'enrolled' AND is_deleted = 0")
    Integer countEnrolledStudents(@Param("courseId") Integer courseId);
    
    /**
     * 查询学生是否已报名该课程
     * 
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 报名记录，如果未报名则返回null
     */
    @Select("SELECT * FROM course_enrollments WHERE course_id = #{courseId} AND student_id = #{studentId} AND status = 'enrolled' AND is_deleted = 0 LIMIT 1")
    CourseEnrollment selectEnrollmentByCourseIdAndStudentId(@Param("courseId") Integer courseId, @Param("studentId") Integer studentId);

    
} 