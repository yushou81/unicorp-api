package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.dto.CourseAttendanceDTO;
import com.csu.unicorp.entity.course.CourseAttendance;
import com.csu.unicorp.entity.course.CourseEnrollment;
import com.csu.unicorp.entity.DualTeacherCourse;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.CourseAttendanceMapper;
import com.csu.unicorp.mapper.CourseEnrollmentMapper;
import com.csu.unicorp.mapper.DualTeacherCourseMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.CourseAttendanceService;
import com.csu.unicorp.vo.CourseAttendanceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 课程出勤服务实现类
 */
@Service
@RequiredArgsConstructor
public class CourseAttendanceServiceImpl implements CourseAttendanceService {

    private final CourseAttendanceMapper attendanceMapper;
    private final DualTeacherCourseMapper courseMapper;
    private final CourseEnrollmentMapper enrollmentMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public boolean recordAttendance(CourseAttendanceDTO attendanceDTO, UserDetails userDetails) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(attendanceDTO.getCourseId());
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("课程不存在");
        }

        // 获取当前用户ID
        Integer userId = Integer.parseInt(userDetails.getUsername());
        
        // 处理学生出勤记录
        for (CourseAttendanceDTO.StudentAttendanceRecord studentRecord : attendanceDTO.getStudentRecords()) {
            Integer studentId = studentRecord.getStudentId();
            
            // 验证学生是否存在
            User student = userMapper.selectById(studentId);
            if (student == null) {
                throw new RuntimeException("学生不存在，ID: " + studentId);
            }

            // 验证学生是否已报名该课程
            LambdaQueryWrapper<CourseEnrollment> enrollmentWrapper = new LambdaQueryWrapper<>();
            enrollmentWrapper.eq(CourseEnrollment::getCourseId, attendanceDTO.getCourseId())
                    .eq(CourseEnrollment::getStudentId, studentId)
                    .eq(CourseEnrollment::getStatus, "enrolled")
                    .eq(CourseEnrollment::getIsDeleted, false);
            
            if (enrollmentMapper.selectCount(enrollmentWrapper) == 0) {
                throw new RuntimeException("学生未报名该课程，ID: " + studentId);
            }

            // 检查是否已有该学生当天的出勤记录
            LambdaQueryWrapper<CourseAttendance> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CourseAttendance::getCourseId, attendanceDTO.getCourseId())
                    .eq(CourseAttendance::getStudentId, studentId)
                    .eq(CourseAttendance::getAttendanceDate, attendanceDTO.getAttendanceDate());
            
            CourseAttendance attendance = attendanceMapper.selectOne(wrapper);
            
            if (attendance != null) {
                // 更新已有记录
                attendance.setStatus(studentRecord.getStatus());
                attendance.setRemark(studentRecord.getRemark());
                attendance.setUpdatedAt(LocalDateTime.now());
                attendanceMapper.updateById(attendance);
            } else {
                // 创建新记录
                attendance = new CourseAttendance();
                attendance.setCourseId(attendanceDTO.getCourseId());
                attendance.setStudentId(studentId);
                attendance.setAttendanceDate(attendanceDTO.getAttendanceDate());
                attendance.setStatus(studentRecord.getStatus());
                attendance.setRemark(studentRecord.getRemark());
                attendance.setRecordedBy(userId);
                attendance.setCreatedAt(LocalDateTime.now());
                attendance.setUpdatedAt(LocalDateTime.now());
                
                attendanceMapper.insert(attendance);
            }
        }
        
        return true;
    }

    @Override
    public List<CourseAttendanceVO> getCourseAttendanceByDate(Integer courseId, LocalDate date, UserDetails userDetails) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("课程不存在");
        }

        // 查询当天出勤记录
        List<CourseAttendance> attendances = attendanceMapper.selectAttendanceByDate(courseId, date);
        
        // 转换为VO
        return attendances.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<CourseAttendanceVO> getStudentAttendance(Integer courseId, Integer studentId, UserDetails userDetails) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("课程不存在");
        }

        // 验证学生是否存在
        User student = userMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 查询学生出勤记录
        List<CourseAttendance> attendances = attendanceMapper.selectAttendanceByStudent(courseId, studentId);
        
        // 转换为VO
        return attendances.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public IPage<LocalDate> getCourseAttendanceDates(Integer courseId, Integer page, Integer size, UserDetails userDetails) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("课程不存在");
        }

        // 分页查询出勤日期
        Page<LocalDate> pageParam = new Page<>(page, size);
        return attendanceMapper.selectAttendanceDatesByCourse(pageParam, courseId);
    }

    @Override
    @Transactional
    public CourseAttendanceVO updateAttendance(Integer attendanceId, String status, String remark, UserDetails userDetails) {
        // 验证出勤记录是否存在
        CourseAttendance attendance = attendanceMapper.selectById(attendanceId);
        if (attendance == null) {
            throw new RuntimeException("出勤记录不存在");
        }

        // 验证用户权限
        if (!hasTeacherOrAdminRole(userDetails)) {
            throw new RuntimeException("无权操作");
        }

        // 更新出勤记录
        attendance.setStatus(status);
        attendance.setRemark(remark);
        attendance.setUpdatedAt(LocalDateTime.now());
        attendanceMapper.updateById(attendance);

        return convertToVO(attendance);
    }

    @Override
    @Transactional
    public boolean deleteAttendance(Integer attendanceId, UserDetails userDetails) {
        // 验证出勤记录是否存在
        CourseAttendance attendance = attendanceMapper.selectById(attendanceId);
        if (attendance == null) {
            throw new RuntimeException("出勤记录不存在");
        }

        // 验证用户权限
        if (!hasTeacherOrAdminRole(userDetails)) {
            throw new RuntimeException("无权操作");
        }

        // 删除出勤记录
        attendanceMapper.deleteById(attendanceId);

        return true;
    }

    @Override
    public Integer calculateCourseAttendanceRate(Integer courseId) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("课程不存在");
        }

        // 获取课程出勤统计
        Integer totalRecords = attendanceMapper.countTotalAttendanceRecords(courseId);
        if (totalRecords == 0) {
            return 0; // 没有记录，出勤率为0
        }
        
        Integer presentRecords = attendanceMapper.countPresentAttendanceRecords(courseId);
        
        // 计算出勤率
        return (int) Math.round((double) presentRecords / totalRecords * 100);
    }

    @Override
    public Integer calculateStudentAttendanceRate(Integer courseId, Integer studentId) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("课程不存在");
        }

        // 验证学生是否存在
        User student = userMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 获取学生出勤统计
        Integer totalDays = attendanceMapper.countTotalAttendanceDays(courseId);
        if (totalDays == 0) {
            return 0; // 没有记录，出勤率为0
        }
        
        Integer presentDays = attendanceMapper.countStudentPresentDays(courseId, studentId);
        
        // 计算出勤率
        return (int) Math.round((double) presentDays / totalDays * 100);
    }

    @Override
    public Map<String, Object> getAttendanceStatistics(Integer courseId, UserDetails userDetails) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("课程不存在");
        }

        // 获取出勤统计数据
        Map<String, Object> result = new HashMap<>();
        
        // 获取总出勤天数
        Integer totalDays = attendanceMapper.countTotalAttendanceDays(courseId);
        result.put("totalDays", totalDays);
        
        // 获取已报名学生数
        Integer enrolledStudents = enrollmentMapper.countEnrolledStudents(courseId);
        result.put("enrolledStudents", enrolledStudents);
        
        // 获取各状态统计
        Integer presentCount = attendanceMapper.countAttendanceByStatus(courseId, "present");
        Integer absentCount = attendanceMapper.countAttendanceByStatus(courseId, "absent");
        Integer lateCount = attendanceMapper.countAttendanceByStatus(courseId, "late");
        Integer leaveCount = attendanceMapper.countAttendanceByStatus(courseId, "leave");
        
        result.put("presentCount", presentCount);
        result.put("absentCount", absentCount);
        result.put("lateCount", lateCount);
        result.put("leaveCount", leaveCount);
        
        // 计算总出勤率
        Integer totalRecords = presentCount + absentCount + lateCount + leaveCount;
        Integer attendanceRate = totalRecords == 0 ? 0 : 
                (int) Math.round((double) presentCount / totalRecords * 100);
        result.put("attendanceRate", attendanceRate);
        
        return result;
    }
    
    /**
     * 判断用户是否有教师或管理员角色
     * @param userDetails 用户
     * @return 是否有教师或管理员角色
     */
    private boolean hasTeacherOrAdminRole(UserDetails userDetails) {
        return userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER")) ||
               userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
    
    /**
     * 将实体转换为VO
     * @param attendance 出勤实体
     * @return 出勤VO
     */
    private CourseAttendanceVO convertToVO(CourseAttendance attendance) {
        CourseAttendanceVO vo = new CourseAttendanceVO();
        BeanUtils.copyProperties(attendance, vo);
        
        // 设置学生信息
        User student = userMapper.selectById(attendance.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getNickname());
        }
        
        // 设置记录人信息
        User recorder = userMapper.selectById(attendance.getRecordedBy());
        if (recorder != null) {
            vo.setRecorderName(recorder.getNickname());
        }
        
        return vo;
    }
} 