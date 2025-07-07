package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.CourseEnrollmentDTO;
import com.csu.unicorp.dto.DualTeacherCourseDTO;
import com.csu.unicorp.entity.course.CourseEnrollment;
import com.csu.unicorp.vo.DualTeacherCourseVO;
import com.csu.unicorp.vo.UserVO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * 双师课堂服务接口
 */
public interface DualTeacherCourseService {

    /**
     * 创建双师课堂课程
     * 
     * @param courseDTO 课程信息
     * @param userDetails 当前登录用户
     * @return 创建成功的课程信息
     */
    DualTeacherCourseVO createCourse(DualTeacherCourseDTO courseDTO, UserDetails userDetails);
    
    /**
     * 更新课程信息
     * 
     * @param id 课程ID
     * @param courseDTO 课程更新信息
     * @param userDetails 当前登录用户
     * @return 更新后的课程信息
     */
    DualTeacherCourseVO updateCourse(Integer id, DualTeacherCourseDTO courseDTO, UserDetails userDetails);
    
    /**
     * 获取课程详情
     * 
     * @param id 课程ID
     * @return 课程详情
     */
    DualTeacherCourseVO getCourseById(Integer id);
    
    /**
     * 删除课程（逻辑删除）
     * 
     * @param id 课程ID
     * @param userDetails 当前登录用户
     */
    void deleteCourse(Integer id, UserDetails userDetails);
    
    /**
     * 获取当前教师创建的课程列表
     * 
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 课程列表
     */
    IPage<DualTeacherCourseVO> getTeacherCourses(int page, int size, UserDetails userDetails);
    
    /**
     * 获取当前企业导师参与的课程列表
     * 
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 课程列表
     */
    IPage<DualTeacherCourseVO> getMentorCourses(int page, int size, UserDetails userDetails);
    
    /**
     * 获取可报名的课程列表
     * 
     * @param page 页码
     * @param size 每页大小
     * @return 可报名课程列表
     */
    IPage<DualTeacherCourseVO> getEnrollableCourses(int page, int size);
    
    /**
     * 学生报名课程
     * 
     * @param enrollmentDTO 报名信息
     * @param userDetails 当前登录用户
     * @return 报名结果
     */
    CourseEnrollment enrollCourse(CourseEnrollmentDTO enrollmentDTO, UserDetails userDetails);
    
    /**
     * 学生取消报名
     * 
     * @param courseId 课程ID
     * @param userDetails 当前登录用户
     */
    void cancelEnrollment(Integer courseId, UserDetails userDetails);
    
    /**
     * 获取学生已报名的课程列表
     * 
     * @param page 页码
     * @param size 每页大小
     * @param userDetails 当前登录用户
     * @return 已报名课程列表
     */
    IPage<DualTeacherCourseVO> getStudentEnrolledCourses(int page, int size, UserDetails userDetails);
    
    /**
     * 更新课程状态
     * 
     * @param id 课程ID
     * @param status 新状态
     * @param userDetails 当前登录用户
     * @return 更新后的课程信息
     */
    DualTeacherCourseVO updateCourseStatus(Integer id, String status, UserDetails userDetails);
    
    /**
     * 更新学生选课状态
     * 
     * @param enrollmentId 选课记录ID
     * @param status 新状态
     * @param userDetails 当前登录用户
     */
    void updateEnrollmentStatus(Integer enrollmentId, String status, UserDetails userDetails);
    
    /**
     * 获取课程学生列表
     * 
     * @param id 课程ID
     * @param userDetails 当前登录用户
     * @return 学生列表
     */
    List<UserVO> getCourseStudents(Integer id, UserDetails userDetails);
    
    /**
     * 检查学生是否已报名课程
     * 
     * @param courseId 课程ID
     * @param userDetails 当前登录用户
     * @return 是否已报名
     */
    boolean isStudentEnrolled(Integer courseId, UserDetails userDetails);
} 