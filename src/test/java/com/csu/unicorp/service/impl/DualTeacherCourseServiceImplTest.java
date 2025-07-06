package com.csu.unicorp.service.impl;

import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.dto.CourseEnrollmentDTO;
import com.csu.unicorp.dto.DualTeacherCourseDTO;
import com.csu.unicorp.entity.course.CourseEnrollment;
import com.csu.unicorp.entity.DualTeacherCourse;
import com.csu.unicorp.entity.organization.Organization;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.course.CourseEnrollmentMapper;
import com.csu.unicorp.mapper.course.DualTeacherCourseMapper;
import com.csu.unicorp.service.OrganizationService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.service.impl.course.DualTeacherCourseServiceImpl;
import com.csu.unicorp.vo.DualTeacherCourseVO;
import com.csu.unicorp.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional  // 使用事务管理，测试后回滚
public class DualTeacherCourseServiceImplTest {

    @Autowired
    private DualTeacherCourseServiceImpl courseService;

    @Autowired
    private DualTeacherCourseMapper courseMapper;

    @Autowired
    private CourseEnrollmentMapper enrollmentMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private OrganizationService organizationService;

    private Organization testSchool;
    private Organization testEnterprise;
    private User testTeacher;
    private User testStudent;
    private User testSchoolAdmin;
    private User testMentor;
    private DualTeacherCourse testCourse;
    private CourseEnrollment testEnrollment;
    private UserDetails teacherDetails;
    private UserDetails studentDetails;
    private UserDetails schoolAdminDetails;
    private UserDetails mentorDetails;

    @BeforeEach
    public void setUp() {
        // 创建测试组织
        testSchool = new Organization();
        testSchool.setId(1);
        testSchool.setOrganizationName("测试学校");
        testSchool.setType("school");

        testEnterprise = new Organization();
        testEnterprise.setId(2);
        testEnterprise.setOrganizationName("测试企业");
        testEnterprise.setType("enterprise");

        // 创建测试用户
        testTeacher = new User();
        testTeacher.setId(1);
        testTeacher.setAccount("teacher");
        testTeacher.setPassword("password");
        testTeacher.setNickname("测试教师");
        testTeacher.setOrganizationId(1);
        testTeacher.setStatus("active");
        testTeacher.setCreatedAt(LocalDateTime.now());
        testTeacher.setUpdatedAt(LocalDateTime.now());
        testTeacher.setIsDeleted(false);

        testStudent = new User();
        testStudent.setId(2);
        testStudent.setAccount("student");
        testStudent.setPassword("password");
        testStudent.setNickname("测试学生");
        testStudent.setOrganizationId(1);
        testStudent.setStatus("active");
        testStudent.setCreatedAt(LocalDateTime.now());
        testStudent.setUpdatedAt(LocalDateTime.now());
        testStudent.setIsDeleted(false);

        testSchoolAdmin = new User();
        testSchoolAdmin.setId(3);
        testSchoolAdmin.setAccount("school_admin");
        testSchoolAdmin.setPassword("password");
        testSchoolAdmin.setNickname("测试学校管理员");
        testSchoolAdmin.setOrganizationId(1);
        testSchoolAdmin.setStatus("active");
        testSchoolAdmin.setCreatedAt(LocalDateTime.now());
        testSchoolAdmin.setUpdatedAt(LocalDateTime.now());
        testSchoolAdmin.setIsDeleted(false);

        testMentor = new User();
        testMentor.setId(4);
        testMentor.setAccount("mentor");
        testMentor.setPassword("password");
        testMentor.setNickname("测试企业导师");
        testMentor.setOrganizationId(2);
        testMentor.setStatus("active");
        testMentor.setCreatedAt(LocalDateTime.now());
        testMentor.setUpdatedAt(LocalDateTime.now());
        testMentor.setIsDeleted(false);

        // 创建测试课程
        testCourse = new DualTeacherCourse();
        testCourse.setTitle("测试课程");
        testCourse.setDescription("这是一个测试课程");
        testCourse.setTeacherId(testTeacher.getId());
        testCourse.setMentorId(testMentor.getId());
        testCourse.setStatus("open");  // 可报名状态
        testCourse.setMaxStudents(30);
        testCourse.setCreatedAt(LocalDateTime.now());
        testCourse.setUpdatedAt(LocalDateTime.now());
        testCourse.setIsDeleted(false);
        courseMapper.insert(testCourse);

        // 创建测试报名记录
        testEnrollment = new CourseEnrollment();
        testEnrollment.setCourseId(testCourse.getId());
        testEnrollment.setStudentId(testStudent.getId());
        testEnrollment.setStatus("enrolled");
        testEnrollment.setEnrollmentTime(LocalDateTime.now());
        testEnrollment.setIsDeleted(false);
        enrollmentMapper.insert(testEnrollment);

        // 创建用户详情
        teacherDetails = createUserDetails(testTeacher.getId().toString(), "ROLE_TEACHER");
        studentDetails = createUserDetails(testStudent.getId().toString(), "ROLE_STUDENT");
        schoolAdminDetails = createUserDetails(testSchoolAdmin.getId().toString(), "ROLE_SCH_ADMIN");
        mentorDetails = createUserDetails(testMentor.getId().toString(), "ROLE_ENTERPRISE_MENTOR");

        // 模拟UserService行为
        when(userService.getByAccount("1")).thenReturn(testTeacher);
        when(userService.getByAccount("2")).thenReturn(testStudent);
        when(userService.getByAccount("3")).thenReturn(testSchoolAdmin);
        when(userService.getByAccount("4")).thenReturn(testMentor);
        when(userService.getById(1)).thenReturn(testTeacher);
        when(userService.getById(2)).thenReturn(testStudent);
        when(userService.getById(3)).thenReturn(testSchoolAdmin);
        when(userService.getById(4)).thenReturn(testMentor);
        when(userService.getUserRole(1)).thenReturn(RoleConstants.DB_ROLE_TEACHER);
        when(userService.getUserRole(2)).thenReturn(RoleConstants.DB_ROLE_STUDENT);
        when(userService.getUserRole(3)).thenReturn(RoleConstants.DB_ROLE_SCHOOL_ADMIN);
        when(userService.getUserRole(4)).thenReturn(RoleConstants.DB_ROLE_ENTERPRISE_MENTOR);

        // 模拟OrganizationService行为
        when(organizationService.getById(1)).thenReturn(testSchool);
        when(organizationService.getById(2)).thenReturn(testEnterprise);
    }

    private UserDetails createUserDetails(String username, String role) {
        return new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(role));
                return authorities;
            }

            @Override
            public String getPassword() {
                return "password";
            }

            @Override
            public String getUsername() {
                return username;
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
    public void testCreateCourse() {
        // 创建课程DTO
        DualTeacherCourseDTO courseDTO = new DualTeacherCourseDTO();
        courseDTO.setTitle("新课程");
        courseDTO.setDescription("这是一个新课程");
        courseDTO.setMaxStudents(30);

        // 创建课程
        DualTeacherCourseVO result = courseService.createCourse(courseDTO, teacherDetails);

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertEquals(courseDTO.getTitle(), result.getTitle(), "课程标题应匹配");
        assertEquals(courseDTO.getDescription(), result.getDescription(), "课程描述应匹配");
        assertEquals(testTeacher.getId(), result.getTeacherId(), "教师ID应匹配");
        assertEquals("planning", result.getStatus(), "课程状态应为规划中");
    }

    @Test
    public void testUpdateCourse() {
        // 创建更新课程DTO
        DualTeacherCourseDTO courseDTO = new DualTeacherCourseDTO();
        courseDTO.setTitle("更新的课程标题");
        courseDTO.setDescription("更新的课程描述");
        courseDTO.setMaxStudents(40);

        // 更新课程
        DualTeacherCourseVO result = courseService.updateCourse(testCourse.getId(), courseDTO, teacherDetails);

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertEquals(courseDTO.getTitle(), result.getTitle(), "课程标题应更新");
        assertEquals(courseDTO.getDescription(), result.getDescription(), "课程描述应更新");
        assertEquals(courseDTO.getMaxStudents(), result.getMaxStudents(), "最大学生数应更新");
    }

    @Test
    public void testGetCourseById() {
        // 获取课程详情
        DualTeacherCourseVO result = courseService.getCourseById(testCourse.getId());

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertEquals(testCourse.getId(), result.getId(), "课程ID应匹配");
        assertEquals(testCourse.getTitle(), result.getTitle(), "课程标题应匹配");
        assertEquals(testCourse.getDescription(), result.getDescription(), "课程描述应匹配");
    }

    @Test
    public void testEnrollCourse() {
        // 先删除已有的报名记录，以便测试
        enrollmentMapper.deleteById(testEnrollment.getId());

        // 创建新的课程，避免唯一键约束冲突
        DualTeacherCourse newCourse = new DualTeacherCourse();
        newCourse.setTitle("新测试课程");
        newCourse.setDescription("这是一个新测试课程");
        newCourse.setTeacherId(testTeacher.getId());
        newCourse.setMentorId(testMentor.getId());
        newCourse.setStatus("open");  // 可报名状态
        newCourse.setMaxStudents(30);
        newCourse.setCreatedAt(LocalDateTime.now());
        newCourse.setUpdatedAt(LocalDateTime.now());
        newCourse.setIsDeleted(false);
        courseMapper.insert(newCourse);

        // 创建报名DTO
        CourseEnrollmentDTO enrollmentDTO = new CourseEnrollmentDTO();
        enrollmentDTO.setCourseId(newCourse.getId());

        // 报名课程
        CourseEnrollment result = courseService.enrollCourse(enrollmentDTO, studentDetails);

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertEquals(newCourse.getId(), result.getCourseId(), "课程ID应匹配");
        assertEquals(testStudent.getId(), result.getStudentId(), "学生ID应匹配");
        assertEquals("enrolled", result.getStatus(), "报名状态应为已报名");
    }

    @Test
    public void testEnrollCourseWhenFull() {
        // 将课程最大学生数设为0
        testCourse.setMaxStudents(0);
        courseMapper.updateById(testCourse);

        // 创建报名DTO
        CourseEnrollmentDTO enrollmentDTO = new CourseEnrollmentDTO();
        enrollmentDTO.setCourseId(testCourse.getId());

        // 断言抛出异常
        assertThrows(BusinessException.class, () -> {
            courseService.enrollCourse(enrollmentDTO, studentDetails);
        }, "应该抛出BusinessException异常");
    }

    @Test
    public void testUpdateCourseStatus() {
        // 更新课程状态
        DualTeacherCourseVO result = courseService.updateCourseStatus(testCourse.getId(), "in_progress", teacherDetails);

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertEquals("in_progress", result.getStatus(), "课程状态应更新");
        
        // 验证数据库中的记录是否更新
        DualTeacherCourse updatedCourse = courseMapper.selectById(testCourse.getId());
        assertNotNull(updatedCourse, "课程应仍然存在于数据库中");
        assertEquals("in_progress", updatedCourse.getStatus(), "数据库中的课程状态应更新");
    }

    @Test
    public void testCancelEnrollment() {
        // 取消报名
        courseService.cancelEnrollment(testCourse.getId(), studentDetails);
        
        // 验证数据库中的记录是否更新
        CourseEnrollment updatedEnrollment = enrollmentMapper.selectById(testEnrollment.getId());
        assertNotNull(updatedEnrollment, "报名记录应仍然存在于数据库中");
        assertEquals("cancelled", updatedEnrollment.getStatus(), "报名状态应为已取消");
    }

    @Test
    public void testGetCourseStudents() {
        // 获取课程学生
        List<UserVO> results = courseService.getCourseStudents(testCourse.getId(), teacherDetails);

        // 断言
        assertNotNull(results, "返回结果不应为空");
        assertFalse(results.isEmpty(), "应该有学生");
    }
} 