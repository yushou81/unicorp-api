package com.csu.unicorp.mapper.course;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.course.CourseAttendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 课程出勤Mapper接口
 */
@Mapper
public interface CourseAttendanceMapper extends BaseMapper<CourseAttendance> {
    
    /**
     * 查询课程指定日期的出勤记录
     * @param courseId 课程ID
     * @param attendanceDate 出勤日期
     * @return 出勤记录列表
     */
    @Select("SELECT * FROM course_attendance WHERE course_id = #{courseId} AND attendance_date = #{attendanceDate} ORDER BY student_id ASC")
    List<CourseAttendance> selectAttendanceByDate(@Param("courseId") Integer courseId, 
                                               @Param("attendanceDate") LocalDate attendanceDate);
    
    /**
     * 查询学生在课程中的所有出勤记录
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 出勤记录列表
     */
    @Select("SELECT * FROM course_attendance WHERE course_id = #{courseId} AND student_id = #{studentId} ORDER BY attendance_date DESC")
    List<CourseAttendance> selectAttendanceByStudent(@Param("courseId") Integer courseId, 
                                                @Param("studentId") Integer studentId);
    
    /**
     * 分页查询课程的所有出勤记录（按日期分组）
     * @param page 分页参数
     * @param courseId 课程ID
     * @return 分页出勤记录列表
     */
    @Select("SELECT DISTINCT attendance_date FROM course_attendance WHERE course_id = #{courseId} ORDER BY attendance_date DESC")
    IPage<LocalDate> selectAttendanceDatesByCourse(Page<LocalDate> page, @Param("courseId") Integer courseId);
    
    /**
     * 统计课程出勤率
     * @param courseId 课程ID
     * @return 出勤率(0-100)
     */
    @Select("SELECT ROUND(SUM(CASE WHEN status = 'present' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) AS attendance_rate " +
            "FROM course_attendance WHERE course_id = #{courseId}")
    Integer calculateCourseAttendanceRate(@Param("courseId") Integer courseId);
    
    /**
     * 统计学生在课程中的出勤率
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 出勤率(0-100)
     */
    @Select("SELECT ROUND(SUM(CASE WHEN status = 'present' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) AS attendance_rate " +
            "FROM course_attendance WHERE course_id = #{courseId} AND student_id = #{studentId}")
    Integer calculateStudentAttendanceRate(@Param("courseId") Integer courseId, 
                                         @Param("studentId") Integer studentId);
    
    /**
     * 统计课程各状态的出勤数量
     * @param courseId 课程ID
     * @return 各状态出勤数量统计
     */
    @Select("SELECT status, COUNT(*) as count FROM course_attendance " +
            "WHERE course_id = #{courseId} GROUP BY status")
    List<Map<String, Object>> countAttendanceStatusByCourse(@Param("courseId") Integer courseId);
    
    /**
     * 统计课程的总出勤记录数
     * @param courseId 课程ID
     * @return 总记录数
     */
    @Select("SELECT COUNT(*) FROM course_attendance WHERE course_id = #{courseId}")
    Integer countTotalAttendanceRecords(@Param("courseId") Integer courseId);
    
    /**
     * 统计课程的出席记录数
     * @param courseId 课程ID
     * @return 出席记录数
     */
    @Select("SELECT COUNT(*) FROM course_attendance WHERE course_id = #{courseId} AND status = 'present'")
    Integer countPresentAttendanceRecords(@Param("courseId") Integer courseId);
    
    /**
     * 统计课程的总出勤天数
     * @param courseId 课程ID
     * @return 总天数
     */
    @Select("SELECT COUNT(DISTINCT attendance_date) FROM course_attendance WHERE course_id = #{courseId}")
    Integer countTotalAttendanceDays(@Param("courseId") Integer courseId);
    
    /**
     * 统计学生在课程中的出席天数
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 出席天数
     */
    @Select("SELECT COUNT(*) FROM course_attendance WHERE course_id = #{courseId} AND student_id = #{studentId} AND status = 'present'")
    Integer countStudentPresentDays(@Param("courseId") Integer courseId, @Param("studentId") Integer studentId);
    
    /**
     * 统计课程中特定状态的记录数
     * @param courseId 课程ID
     * @param status 出勤状态
     * @return 记录数
     */
    @Select("SELECT COUNT(*) FROM course_attendance WHERE course_id = #{courseId} AND status = #{status}")
    Integer countAttendanceByStatus(@Param("courseId") Integer courseId, @Param("status") String status);
} 