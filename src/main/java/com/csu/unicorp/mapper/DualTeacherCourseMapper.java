package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.course.DualTeacherCourse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 双师课堂Mapper接口
 */
@Mapper
public interface DualTeacherCourseMapper extends BaseMapper<DualTeacherCourse> {

    /**
     * 根据教师ID查询课程
     * 
     * @param teacherId 教师ID
     * @param page 分页参数
     * @return 课程列表
     */
    @Select("SELECT * FROM dual_teacher_courses WHERE teacher_id = #{teacherId} AND is_deleted = 0")
    IPage<DualTeacherCourse> selectCoursesByTeacherId(@Param("teacherId") Integer teacherId, Page<DualTeacherCourse> page);
    
    /**
     * 根据企业导师ID查询课程
     * 
     * @param mentorId 企业导师ID
     * @param page 分页参数
     * @return 课程列表
     */
    @Select("SELECT * FROM dual_teacher_courses WHERE mentor_id = #{mentorId} AND is_deleted = 0")
    IPage<DualTeacherCourse> selectCoursesByMentorId(@Param("mentorId") Integer mentorId, Page<DualTeacherCourse> page);
    
    /**
     * 根据状态查询课程
     * 
     * @param status 课程状态
     * @param page 分页参数
     * @return 课程列表
     */
    @Select("SELECT * FROM dual_teacher_courses WHERE status = #{status} AND is_deleted = 0")
    IPage<DualTeacherCourse> selectCoursesByStatus(@Param("status") String status, Page<DualTeacherCourse> page);
    
    /**
     * 获取所有可报名的课程
     * 
     * @param page 分页参数
     * @return 可报名的课程列表
     */
    @Select("SELECT * FROM dual_teacher_courses WHERE status = 'open' AND is_deleted = 0")
    IPage<DualTeacherCourse> selectEnrollableCourses(Page<DualTeacherCourse> page);
} 