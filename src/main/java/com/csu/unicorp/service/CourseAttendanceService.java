package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.CourseAttendanceDTO;
import com.csu.unicorp.vo.CourseAttendanceVO;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 课程出勤服务接口
 */
public interface CourseAttendanceService {
    
    /**
     * 记录课程出勤
     * @param attendanceDTO 出勤信息
     * @param userDetails 当前用户
     * @return 是否记录成功
     */
    boolean recordAttendance(CourseAttendanceDTO attendanceDTO, UserDetails userDetails);
    
    /**
     * 获取课程某天的出勤记录
     * @param courseId 课程ID
     * @param date 日期
     * @param userDetails 当前用户
     * @return 出勤记录列表
     */
    List<CourseAttendanceVO> getCourseAttendanceByDate(Integer courseId, LocalDate date, UserDetails userDetails);
    
    /**
     * 获取学生在课程中的出勤记录
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @param userDetails 当前用户
     * @return 出勤记录列表
     */
    List<CourseAttendanceVO> getStudentAttendance(Integer courseId, Integer studentId, UserDetails userDetails);
    
    /**
     * 分页获取课程的出勤日期列表
     * @param courseId 课程ID
     * @param page 页码
     * @param size 每页数量
     * @param userDetails 当前用户
     * @return 分页出勤日期列表
     */
    IPage<LocalDate> getCourseAttendanceDates(Integer courseId, Integer page, Integer size, UserDetails userDetails);
    
    /**
     * 更新出勤记录
     * @param attendanceId 出勤记录ID
     * @param status 出勤状态
     * @param remark 备注
     * @param userDetails 当前用户
     * @return 更新后的出勤记录
     */
    CourseAttendanceVO updateAttendance(Integer attendanceId, String status, String remark, UserDetails userDetails);
    
    /**
     * 删除出勤记录
     * @param attendanceId 出勤记录ID
     * @param userDetails 当前用户
     * @return 是否删除成功
     */
    boolean deleteAttendance(Integer attendanceId, UserDetails userDetails);
    
    /**
     * 统计课程的出勤率
     * @param courseId 课程ID
     * @return 出勤率(0-100)
     */
    Integer calculateCourseAttendanceRate(Integer courseId);
    
    /**
     * 统计学生在课程中的出勤率
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 出勤率(0-100)
     */
    Integer calculateStudentAttendanceRate(Integer courseId, Integer studentId);
    
    /**
     * 获取课程出勤统计数据
     * @param courseId 课程ID
     * @param userDetails 当前用户
     * @return 出勤统计数据
     */
    Map<String, Object> getAttendanceStatistics(Integer courseId, UserDetails userDetails);
} 