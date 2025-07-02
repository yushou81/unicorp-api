package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.dto.CourseEnrollmentDTO;
import com.csu.unicorp.dto.DualTeacherCourseDTO;
import com.csu.unicorp.entity.CourseEnrollment;
import com.csu.unicorp.entity.DualTeacherCourse;
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.CourseEnrollmentMapper;
import com.csu.unicorp.mapper.DualTeacherCourseMapper;
import com.csu.unicorp.service.DualTeacherCourseService;
import com.csu.unicorp.service.OrganizationService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.DualTeacherCourseVO;
import com.csu.unicorp.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 双师课堂服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DualTeacherCourseServiceImpl implements DualTeacherCourseService {

    private final DualTeacherCourseMapper courseMapper;
    private final CourseEnrollmentMapper enrollmentMapper;
    private final UserService userService;
    private final OrganizationService organizationService;

    @Override
    @Transactional
    public DualTeacherCourseVO createCourse(DualTeacherCourseDTO courseDTO, UserDetails userDetails) {
        // 获取当前用户信息
        User currentUser = userService.getByAccount(userDetails.getUsername());
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户权限
        String role = userService.getUserRole(currentUser.getId());
        if (!RoleConstants.DB_ROLE_TEACHER.equals(role) && !RoleConstants.DB_ROLE_SCHOOL_ADMIN.equals(role)) {
            throw new BusinessException("权限不足，只有教师或学校管理员可以创建双师课堂");
        }

        // 创建课程
        DualTeacherCourse course = new DualTeacherCourse();
        BeanUtils.copyProperties(courseDTO, course);
        
        // 如果没有指定教师，则设置为当前用户
        if (course.getTeacherId() == null) {
            if (RoleConstants.DB_ROLE_TEACHER.equals(role)) {
                course.setTeacherId(currentUser.getId());
            } else {
                throw new BusinessException("请指定课程负责教师");
            }
        }
        
        // 设置课程初始状态为规划中
        course.setStatus("planning");
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());
        course.setIsDeleted(false);
        
        // 保存课程
        courseMapper.insert(course);
        
        // 返回VO
        return convertToVO(course);
    }

    @Override
    @Transactional
    public DualTeacherCourseVO updateCourse(Integer id, DualTeacherCourseDTO courseDTO, UserDetails userDetails) {
        // 获取当前用户信息
        User currentUser = userService.getByAccount(userDetails.getUsername());
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }
        log.info("Current user: {}", currentUser);

        // 获取课程信息
        DualTeacherCourse course = courseMapper.selectById(id);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new BusinessException("课程不存在");
        }

        // 检查用户权限
        String role = userService.getUserRole(currentUser.getId());
        log.info("Role: {}", role);
        log.info("TeacherId: {}", course.getTeacherId());
        log.info("MentorId: {}", course.getMentorId());
        boolean isTeacher = RoleConstants.DB_ROLE_TEACHER.equals(role) && currentUser.getId().equals(course.getTeacherId());
        boolean isSchoolAdmin = RoleConstants.DB_ROLE_SCHOOL_ADMIN.equals(role)
                && currentUser.getOrganizationId() != null
                && currentUser.getOrganizationId().equals(getUserOrganizationId(course.getTeacherId()));

        if (!isTeacher && !isSchoolAdmin) {
            throw new BusinessException("权限不足，只有课程教师或学校管理员可以删除课程");
        }

        // 更新课程信息
        BeanUtils.copyProperties(courseDTO, course);
        course.setUpdatedAt(LocalDateTime.now());
        
        // 保存更新
        courseMapper.updateById(course);
        
        // 返回更新后的VO
        return convertToVO(course);
    }

    @Override
    public DualTeacherCourseVO getCourseById(Integer id) {
        // 获取课程信息
        DualTeacherCourse course = courseMapper.selectById(id);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new BusinessException("课程不存在");
        }

        return convertToVO(course);
    }

    @Override
    @Transactional
    public void deleteCourse(Integer id, UserDetails userDetails) {
        // 获取当前用户信息
        User currentUser = userService.getByAccount(userDetails.getUsername());
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 获取课程信息
        DualTeacherCourse course = courseMapper.selectById(id);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new BusinessException("课程不存在");
        }

        // 检查用户权限
        String role = userService.getUserRole(currentUser.getId());
        boolean isTeacher = RoleConstants.DB_ROLE_TEACHER.equals(role) && currentUser.getId().equals(course.getTeacherId());
        boolean isSchoolAdmin = RoleConstants.DB_ROLE_SCHOOL_ADMIN.equals(role) 
                && currentUser.getOrganizationId() != null
                && currentUser.getOrganizationId().equals(getUserOrganizationId(course.getTeacherId()));
        
        if (!isTeacher && !isSchoolAdmin) {
            throw new BusinessException("权限不足，只有课程教师或学校管理员可以删除课程");
        }

        // 逻辑删除课程
        courseMapper.deleteById(id);
    }

    @Override
    public IPage<DualTeacherCourseVO> getTeacherCourses(int page, int size, UserDetails userDetails) {
        // 获取当前用户信息
        User currentUser = userService.getByAccount(userDetails.getUsername());
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户角色
        String role = userService.getUserRole(currentUser.getId());
        if (!RoleConstants.DB_ROLE_TEACHER.equals(role)) {
            throw new BusinessException("权限不足，只有教师可以查看自己的课程");
        }

        // 获取教师课程列表
        Page<DualTeacherCourse> pageParam = new Page<>(page, size);
        IPage<DualTeacherCourse> courseIPage = courseMapper.selectCoursesByTeacherId(currentUser.getId(), pageParam);
        
        // 转换为VO
        return courseIPage.convert(this::convertToVO);
    }

    @Override
    public IPage<DualTeacherCourseVO> getMentorCourses(int page, int size, UserDetails userDetails) {
        // 获取当前用户信息
        User currentUser = userService.getByAccount(userDetails.getUsername());
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户角色
        String role = userService.getUserRole(currentUser.getId());
        if (!RoleConstants.DB_ROLE_ENTERPRISE_MENTOR.equals(role)) {
            throw new BusinessException("权限不足，只有企业导师可以查看自己的课程");
        }

        // 获取企业导师课程列表
        Page<DualTeacherCourse> pageParam = new Page<>(page, size);
        IPage<DualTeacherCourse> courseIPage = courseMapper.selectCoursesByMentorId(currentUser.getId(), pageParam);
        
        // 转换为VO
        return courseIPage.convert(this::convertToVO);
    }

    @Override
    public IPage<DualTeacherCourseVO> getEnrollableCourses(int page, int size) {
        // 获取可报名的课程列表
        Page<DualTeacherCourse> pageParam = new Page<>(page, size);
        IPage<DualTeacherCourse> courseIPage = courseMapper.selectEnrollableCourses(pageParam);
        
        // 转换为VO
        return courseIPage.convert(this::convertToVO);
    }

    @Override
    @Transactional
    public CourseEnrollment enrollCourse(CourseEnrollmentDTO enrollmentDTO, UserDetails userDetails) {
        // 获取当前用户信息
        User currentUser = userService.getByAccount(userDetails.getUsername());
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户角色
        String role = userService.getUserRole(currentUser.getId());
        if (!RoleConstants.DB_ROLE_STUDENT.equals(role)) {
            throw new BusinessException("权限不足，只有学生可以报名课程");
        }

        // 获取课程信息
        DualTeacherCourse course = courseMapper.selectById(enrollmentDTO.getCourseId());
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new BusinessException("课程不存在");
        }

        // 检查课程状态是否开放报名
        if (!"open".equals(course.getStatus())) {
            throw new BusinessException("该课程当前不可报名");
        }

        // 检查是否已经报名
        CourseEnrollment existingEnrollment = enrollmentMapper.selectEnrollmentByCourseIdAndStudentId(
                enrollmentDTO.getCourseId(), currentUser.getId());
        if (existingEnrollment != null) {
            throw new BusinessException("您已经报名了该课程");
        }

        // 检查课程人数是否已满
        Integer enrolledCount = enrollmentMapper.countEnrollmentsByCourseId(enrollmentDTO.getCourseId());
        if (enrolledCount >= course.getMaxStudents()) {
            throw new BusinessException("课程已达到最大人数限制，无法报名");
        }

        // 创建报名记录
        CourseEnrollment enrollment = new CourseEnrollment();
        enrollment.setCourseId(enrollmentDTO.getCourseId());
        enrollment.setStudentId(currentUser.getId());
        enrollment.setStatus("enrolled");
        enrollment.setEnrollmentTime(LocalDateTime.now());
        enrollment.setIsDeleted(false);
        
        // 保存报名记录
        enrollmentMapper.insert(enrollment);
        
        return enrollment;
    }

    @Override
    @Transactional
    public void cancelEnrollment(Integer courseId, UserDetails userDetails) {
        // 获取当前用户信息
        User currentUser = userService.getByAccount(userDetails.getUsername());
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 获取报名记录
        CourseEnrollment enrollment = enrollmentMapper.selectEnrollmentByCourseIdAndStudentId(courseId, currentUser.getId());
        if (enrollment == null) {
            throw new BusinessException("您未报名该课程");
        }

        // 获取课程信息
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new BusinessException("课程不存在");
        }

        // 检查课程是否已经开始
        if ("in_progress".equals(course.getStatus()) || "completed".equals(course.getStatus())) {
            throw new BusinessException("课程已开始或已结束，无法取消报名");
        }

        // 更新报名状态为取消
        enrollment.setStatus("cancelled");
        enrollmentMapper.updateById(enrollment);
    }

    @Override
    public IPage<DualTeacherCourseVO> getStudentEnrolledCourses(int page, int size, UserDetails userDetails) {
        // 获取当前用户信息
        User currentUser = userService.getByAccount(userDetails.getUsername());
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查用户角色
        String role = userService.getUserRole(currentUser.getId());
        if (!RoleConstants.DB_ROLE_STUDENT.equals(role)) {
            throw new BusinessException("权限不足，只有学生可以查看自己报名的课程");
        }

        // 获取学生报名的课程列表
        Page<CourseEnrollment> pageParam = new Page<>(page, size);
        IPage<CourseEnrollment> enrollmentIPage = enrollmentMapper.selectEnrollmentsByStudentId(currentUser.getId(), pageParam);
        
        // 转换为课程VO
        return enrollmentIPage.convert(enrollment -> {
            DualTeacherCourse course = courseMapper.selectById(enrollment.getCourseId());
            return convertToVO(course);
        });
    }

    @Override
    @Transactional
    public DualTeacherCourseVO updateCourseStatus(Integer id, String status, UserDetails userDetails) {
        // 获取当前用户信息
        User currentUser = userService.getByAccount(userDetails.getUsername());
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 获取课程信息
        DualTeacherCourse course = courseMapper.selectById(id);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new BusinessException("课程不存在");
        }

        // 检查用户权限
        String role = userService.getUserRole(currentUser.getId());
        boolean isTeacher = RoleConstants.DB_ROLE_TEACHER.equals(role) && currentUser.getId().equals(course.getTeacherId());
        boolean isSchoolAdmin = RoleConstants.DB_ROLE_SCHOOL_ADMIN.equals(role) 
                && currentUser.getOrganizationId() != null
                && currentUser.getOrganizationId().equals(getUserOrganizationId(course.getTeacherId()));
        
        if (!isTeacher && !isSchoolAdmin) {
            throw new BusinessException("权限不足，只有课程教师或学校管理员可以更新课程状态");
        }

        // 验证状态值是否有效
        if (!isValidCourseStatus(status)) {
            throw new BusinessException("无效的课程状态值");
        }

        // 更新课程状态
        course.setStatus(status);
        course.setUpdatedAt(LocalDateTime.now());
        courseMapper.updateById(course);
        
        return convertToVO(course);
    }

    @Override
    @Transactional
    public void updateEnrollmentStatus(Integer enrollmentId, String status, UserDetails userDetails) {
        // 获取当前用户信息
        User currentUser = userService.getByAccount(userDetails.getUsername());
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 获取选课记录
        CourseEnrollment enrollment = enrollmentMapper.selectById(enrollmentId);
        if (enrollment == null || Boolean.TRUE.equals(enrollment.getIsDeleted())) {
            throw new BusinessException("选课记录不存在");
        }
        
        // 获取课程信息
        DualTeacherCourse course = courseMapper.selectById(enrollment.getCourseId());
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new BusinessException("课程不存在");
        }
        
        // 检查用户权限
        String role = userService.getUserRole(currentUser.getId());
        boolean isTeacher = RoleConstants.DB_ROLE_TEACHER.equals(role) && currentUser.getId().equals(course.getTeacherId());
        boolean isSchoolAdmin = RoleConstants.DB_ROLE_SCHOOL_ADMIN.equals(role) 
                && currentUser.getOrganizationId() != null
                && currentUser.getOrganizationId().equals(getUserOrganizationId(course.getTeacherId()));
        
        if (!isTeacher && !isSchoolAdmin) {
            throw new BusinessException("权限不足，只有课程教师或学校管理员可以更新选课状态");
        }
        
        // 验证状态值是否有效
        if (!isValidEnrollmentStatus(status)) {
            throw new BusinessException("无效的选课状态值");
        }
        
        // 更新选课状态
        enrollment.setStatus(status);
        enrollmentMapper.updateById(enrollment);
    }

    /**
     * 验证课程状态值是否有效
     */
    private boolean isValidCourseStatus(String status) {
        return status != null && (
                "planning".equals(status) || 
                "open".equals(status) || 
                "in_progress".equals(status) || 
                "completed".equals(status) || 
                "cancelled".equals(status)
        );
    }
    
    /**
     * 验证选课状态值是否有效
     */
    private boolean isValidEnrollmentStatus(String status) {
        return status != null && (
                "enrolled".equals(status) || 
                "cancelled".equals(status) || 
                "completed".equals(status)
        );
    }

    /**
     * 将实体转换为VO
     */
    private DualTeacherCourseVO convertToVO(DualTeacherCourse course) {
        if (course == null) {
            return null;
        }
        log.info("convertToVO: {}", course);
        // 获取教师和导师信息
        User teacher = course.getTeacherId() != null ? userService.getById(course.getTeacherId()) : null;
        User mentor = course.getMentorId() != null ? userService.getById(course.getMentorId()) : null;
        
        // 获取企业信息
        String enterpriseName = null;
        if (mentor != null && mentor.getOrganizationId() != null) {
            Organization organization = organizationService.getById(mentor.getOrganizationId());
            if (organization != null) {
                enterpriseName = organization.getOrganizationName();
            }
        }
        
        // 获取课程报名人数
        Integer enrolledCount = enrollmentMapper.countEnrollmentsByCourseId(course.getId());
        
        // 构建VO
        return DualTeacherCourseVO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .teacherId(course.getTeacherId())
                .teacherName(teacher != null ? teacher.getNickname() : null)
                .mentorId(course.getMentorId())
                .mentorName(mentor != null ? mentor.getNickname() : null)
                .enterpriseName(enterpriseName)
                .scheduledTime(course.getScheduledTime())
                .maxStudents(course.getMaxStudents())
                .enrolledCount(enrolledCount)
                .location(course.getLocation())
                .courseType(course.getCourseType())
                .status(course.getStatus())
                .createdAt(course.getCreatedAt())
                .build();
    }

    /**
     * 获取用户所属组织ID
     */
    private Integer getUserOrganizationId(Integer userId) {
        if (userId == null) {
            return null;
        }
        User user = userService.getById(userId);
        return user != null ? user.getOrganizationId() : null;
    }

    @Override
    public List<UserVO> getCourseStudents(Integer courseId, UserDetails userDetails) {
        // 获取当前用户信息
        User currentUser = userService.getByAccount(userDetails.getUsername());
        if (currentUser == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 获取课程信息
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new BusinessException("课程不存在");
        }
        
        // 检查用户权限
        String role = userService.getUserRole(currentUser.getId());
        boolean isTeacher = RoleConstants.DB_ROLE_TEACHER.equals(role) && currentUser.getId().equals(course.getTeacherId());
        boolean isMentor = RoleConstants.DB_ROLE_ENTERPRISE_MENTOR.equals(role) && currentUser.getId().equals(course.getMentorId());
        boolean isSchoolAdmin = RoleConstants.DB_ROLE_SCHOOL_ADMIN.equals(role) 
                && currentUser.getOrganizationId() != null
                && currentUser.getOrganizationId().equals(getUserOrganizationId(course.getTeacherId()));
        
        if (!isTeacher && !isMentor && !isSchoolAdmin) {
            throw new BusinessException("权限不足，只有课程教师、企业导师或学校管理员可以查看学生列表");
        }
        
        // 获取课程报名记录
        // 使用无分页查询，获取所有学生
        LambdaQueryWrapper<CourseEnrollment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CourseEnrollment::getCourseId, courseId)
               .eq(CourseEnrollment::getStatus, "enrolled") // 只获取已报名且未取消的学生
               .eq(CourseEnrollment::getIsDeleted, false);
        List<CourseEnrollment> enrollments = enrollmentMapper.selectList(wrapper);
        
        // 根据学生ID获取学生信息
        List<UserVO> students = new ArrayList<>();
        for (CourseEnrollment enrollment : enrollments) {
            User student = userService.getById(enrollment.getStudentId());
            if (student != null) {
                // 转换为UserVO
                UserVO studentVO = new UserVO();
                studentVO.setId(student.getId());
                studentVO.setAccount(student.getAccount());
                studentVO.setNickname(student.getNickname());
                studentVO.setEmail(student.getEmail());
                studentVO.setPhone(student.getPhone());
                studentVO.setAvatar(student.getAvatar());
                studentVO.setStatus(student.getStatus());
                studentVO.setOrganizationId(student.getOrganizationId());
                
                // 获取学生角色
                String studentRole = userService.getUserRole(student.getId());
                studentVO.setRole(studentRole);
                
                // 获取组织名称
                if (student.getOrganizationId() != null) {
                    Organization organization = organizationService.getById(student.getOrganizationId());
                    if (organization != null) {
                        studentVO.setOrganizationName(organization.getOrganizationName());
                    }
                }
                
                students.add(studentVO);
            }
        }
        
        return students;
    }
} 