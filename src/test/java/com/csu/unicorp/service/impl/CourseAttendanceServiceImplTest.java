package com.csu.unicorp.service.impl;

import com.csu.unicorp.dto.CourseAttendanceDTO;
import com.csu.unicorp.entity.CourseAttendance;
import com.csu.unicorp.entity.CourseEnrollment;
import com.csu.unicorp.entity.DualTeacherCourse;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.CourseAttendanceMapper;
import com.csu.unicorp.mapper.CourseEnrollmentMapper;
import com.csu.unicorp.mapper.DualTeacherCourseMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.vo.CourseAttendanceVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional  // 使用事务管理，测试后回滚
public class CourseAttendanceServiceImplTest {

    @Autowired
    private CourseAttendanceServiceImpl courseAttendanceService;

    @Autowired
    private DualTeacherCourseMapper courseMapper;

    @Autowired
    private CourseEnrollmentMapper enrollmentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CourseAttendanceMapper attendanceMapper;

    private DualTeacherCourse testCourse;
    private User testTeacher;
    private User testStudent;
    private CourseEnrollment testEnrollment;
    private UserDetails teacherDetails;

    @BeforeEach
    public void setUp() {
        // 创建测试教师
        testTeacher = new User();
        testTeacher.setAccount("test_teacher");
        testTeacher.setPassword("password");
        testTeacher.setNickname("测试教师");
        testTeacher.setEmail("teacher@test.com");
        testTeacher.setStatus("active");
        testTeacher.setCreatedAt(LocalDateTime.now());
        testTeacher.setUpdatedAt(LocalDateTime.now());
        testTeacher.setIsDeleted(false);
        userMapper.insert(testTeacher);

        // 创建测试学生
        testStudent = new User();
        testStudent.setAccount("test_student");
        testStudent.setPassword("password");
        testStudent.setNickname("测试学生");
        testStudent.setEmail("student@test.com");
        testStudent.setStatus("active");
        testStudent.setCreatedAt(LocalDateTime.now());
        testStudent.setUpdatedAt(LocalDateTime.now());
        testStudent.setIsDeleted(false);
        userMapper.insert(testStudent);

        // 创建测试课程
        testCourse = new DualTeacherCourse();
        testCourse.setTitle("测试课程");
        testCourse.setDescription("这是一个测试课程");
        testCourse.setTeacherId(testTeacher.getId());
        testCourse.setStatus("in_progress");
        testCourse.setMaxStudents(30);
        testCourse.setCreatedAt(LocalDateTime.now());
        testCourse.setUpdatedAt(LocalDateTime.now());
        testCourse.setIsDeleted(false);
        courseMapper.insert(testCourse);

        // 创建课程报名记录
        testEnrollment = new CourseEnrollment();
        testEnrollment.setCourseId(testCourse.getId());
        testEnrollment.setStudentId(testStudent.getId());
        testEnrollment.setStatus("enrolled");
        testEnrollment.setEnrollmentTime(LocalDateTime.now());
        testEnrollment.setIsDeleted(false);
        enrollmentMapper.insert(testEnrollment);

        // 创建一个带有ROLE_TEACHER权限的UserDetails实现
        teacherDetails = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_TEACHER"));
                return authorities;
            }

            @Override
            public String getPassword() {
                return "password";
            }

            @Override
            public String getUsername() {
                return testTeacher.getId().toString();
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }

    @Test
    public void testRecordAttendance() {
        // 创建出勤记录DTO
        CourseAttendanceDTO attendanceDTO = new CourseAttendanceDTO();
        attendanceDTO.setCourseId(testCourse.getId());
        attendanceDTO.setAttendanceDate(LocalDate.now());

        CourseAttendanceDTO.StudentAttendanceRecord studentRecord = new CourseAttendanceDTO.StudentAttendanceRecord();
        studentRecord.setStudentId(testStudent.getId());
        studentRecord.setStatus("present");
        studentRecord.setRemark("准时到达");

        List<CourseAttendanceDTO.StudentAttendanceRecord> studentRecords = new ArrayList<>();
        studentRecords.add(studentRecord);
        attendanceDTO.setStudentRecords(studentRecords);

        // 记录出勤
        boolean result = courseAttendanceService.recordAttendance(attendanceDTO, teacherDetails);

        // 断言
        assertTrue(result, "记录出勤应成功");

        // 验证数据库中是否有记录
        List<CourseAttendance> attendances = attendanceMapper.selectAttendanceByDate(testCourse.getId(), LocalDate.now());
        assertFalse(attendances.isEmpty(), "应该存在出勤记录");
        assertEquals("present", attendances.get(0).getStatus(), "出勤状态应为present");
        assertEquals("准时到达", attendances.get(0).getRemark(), "出勤备注应匹配");
    }

    @Test
    public void testGetCourseAttendanceByDate() {
        // 先记录一条出勤数据
        CourseAttendance attendance = new CourseAttendance();
        attendance.setCourseId(testCourse.getId());
        attendance.setStudentId(testStudent.getId());
        attendance.setAttendanceDate(LocalDate.now());
        attendance.setStatus("present");
        attendance.setRemark("测试备注");
        attendance.setRecordedBy(testTeacher.getId());
        attendance.setCreatedAt(LocalDateTime.now());
        attendance.setUpdatedAt(LocalDateTime.now());
        attendanceMapper.insert(attendance);

        // 获取当日出勤记录
        List<CourseAttendanceVO> result = courseAttendanceService.getCourseAttendanceByDate(
                testCourse.getId(), LocalDate.now(), teacherDetails);

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertFalse(result.isEmpty(), "应该有出勤记录");
        assertEquals(testStudent.getId(), result.get(0).getStudentId(), "学生ID应匹配");
        assertEquals("present", result.get(0).getStatus(), "出勤状态应匹配");
    }

    @Test
    public void testGetAttendanceStatistics() {
        // 先记录一些出勤数据
        CourseAttendance attendance1 = new CourseAttendance();
        attendance1.setCourseId(testCourse.getId());
        attendance1.setStudentId(testStudent.getId());
        attendance1.setAttendanceDate(LocalDate.now());
        attendance1.setStatus("present");
        attendance1.setRecordedBy(testTeacher.getId());
        attendance1.setCreatedAt(LocalDateTime.now());
        attendance1.setUpdatedAt(LocalDateTime.now());
        attendanceMapper.insert(attendance1);

        CourseAttendance attendance2 = new CourseAttendance();
        attendance2.setCourseId(testCourse.getId());
        attendance2.setStudentId(testStudent.getId());
        attendance2.setAttendanceDate(LocalDate.now().minusDays(1));
        attendance2.setStatus("absent");
        attendance2.setRecordedBy(testTeacher.getId());
        attendance2.setCreatedAt(LocalDateTime.now());
        attendance2.setUpdatedAt(LocalDateTime.now());
        attendanceMapper.insert(attendance2);

        // 获取统计信息
        Map<String, Object> statistics = courseAttendanceService.getAttendanceStatistics(testCourse.getId(), teacherDetails);

        // 断言
        assertNotNull(statistics, "统计信息不应为空");
        assertEquals(2, statistics.get("totalDays"), "总天数应为2");
        assertEquals(1, statistics.get("presentCount"), "出席次数应为1");
        assertEquals(1, statistics.get("absentCount"), "缺席次数应为1");
    }

    @Test
    public void testCalculateStudentAttendanceRate() {
        // 先记录一些出勤数据
        CourseAttendance attendance1 = new CourseAttendance();
        attendance1.setCourseId(testCourse.getId());
        attendance1.setStudentId(testStudent.getId());
        attendance1.setAttendanceDate(LocalDate.now());
        attendance1.setStatus("present");
        attendance1.setRecordedBy(testTeacher.getId());
        attendance1.setCreatedAt(LocalDateTime.now());
        attendance1.setUpdatedAt(LocalDateTime.now());
        attendanceMapper.insert(attendance1);

        CourseAttendance attendance2 = new CourseAttendance();
        attendance2.setCourseId(testCourse.getId());
        attendance2.setStudentId(testStudent.getId());
        attendance2.setAttendanceDate(LocalDate.now().minusDays(1));
        attendance2.setStatus("absent");
        attendance2.setRecordedBy(testTeacher.getId());
        attendance2.setCreatedAt(LocalDateTime.now());
        attendance2.setUpdatedAt(LocalDateTime.now());
        attendanceMapper.insert(attendance2);

        // 计算学生出勤率
        Integer rate = courseAttendanceService.calculateStudentAttendanceRate(testCourse.getId(), testStudent.getId());

        // 断言 - 出勤率应为50%（1/2）
        assertEquals(50, rate, "学生出勤率应为50%");
    }
} 