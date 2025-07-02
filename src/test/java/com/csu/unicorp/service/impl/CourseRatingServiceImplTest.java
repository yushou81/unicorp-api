package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.dto.CourseRatingDTO;
import com.csu.unicorp.entity.CourseEnrollment;
import com.csu.unicorp.entity.CourseRating;
import com.csu.unicorp.entity.DualTeacherCourse;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.CourseEnrollmentMapper;
import com.csu.unicorp.mapper.CourseRatingMapper;
import com.csu.unicorp.mapper.DualTeacherCourseMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.CourseRatingVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional  // 使用事务管理，测试后回滚
public class CourseRatingServiceImplTest {

    @Autowired
    private CourseRatingServiceImpl ratingService;

    @Autowired
    private CourseRatingMapper ratingMapper;

    @Autowired
    private CourseEnrollmentMapper enrollmentMapper;

    @Autowired
    private DualTeacherCourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    @MockBean
    private UserService userService;

    private DualTeacherCourse testCourse;
    private User testStudent;
    private CourseEnrollment testEnrollment;
    private CourseRating testRating;
    private UserDetails studentDetails;

    @BeforeEach
    public void setUp() {
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
        testCourse.setStatus("completed");  // 已完成的课程可以评价
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

        // 创建测试评价
        testRating = new CourseRating();
        testRating.setCourseId(testCourse.getId());
        testRating.setStudentId(testStudent.getId());
        testRating.setRating(4);  // 4星评价
        testRating.setComment("这是一个测试评价");
        testRating.setIsAnonymous(false);
        testRating.setCreatedAt(LocalDateTime.now());
        testRating.setUpdatedAt(LocalDateTime.now());
        testRating.setIsDeleted(false);
        ratingMapper.insert(testRating);

        // 创建学生用户详情
        studentDetails = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
                return authorities;
            }

            @Override
            public String getPassword() {
                return "password";
            }

            @Override
            public String getUsername() {
                return testStudent.getId().toString();
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

        // 模拟UserService行为
        when(userService.getByAccount(anyString())).thenReturn(testStudent);
        when(userService.getById(testStudent.getId())).thenReturn(testStudent);
    }

    @Test
    public void testSubmitRating() {
        // 先删除已有的评价，以便测试
        ratingMapper.deleteById(testRating.getId());

        // 创建评价DTO
        CourseRatingDTO ratingDTO = new CourseRatingDTO();
        ratingDTO.setCourseId(testCourse.getId());
        ratingDTO.setRating(5);
        ratingDTO.setComment("这是一个很好的课程");
        ratingDTO.setIsAnonymous(false);

        // 提交评价
        CourseRatingVO result = ratingService.submitRating(ratingDTO, studentDetails);

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertEquals(ratingDTO.getRating(), result.getRating(), "评价星级应匹配");
        assertEquals(ratingDTO.getComment(), result.getComment(), "评价内容应匹配");
        assertEquals(testStudent.getId(), result.getStudentId(), "学生ID应匹配");
        assertEquals(testStudent.getNickname(), result.getStudentName(), "学生名称应匹配");
    }

    @Test
    public void testUpdateRating() {
        // 创建更新评价DTO
        CourseRatingDTO ratingDTO = new CourseRatingDTO();
        ratingDTO.setRating(3);
        ratingDTO.setComment("更新后的评价内容");
        ratingDTO.setIsAnonymous(true);

        // 更新评价
        CourseRatingVO result = ratingService.updateRating(testRating.getId(), ratingDTO, studentDetails);

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertEquals(ratingDTO.getRating(), result.getRating(), "评价星级应更新");
        assertEquals(ratingDTO.getComment(), result.getComment(), "评价内容应更新");
        assertTrue(result.getIsAnonymous(), "匿名状态应更新");
        assertEquals("匿名用户", result.getStudentName(), "匿名评价应显示为匿名用户");
        
        // 验证数据库中的记录是否更新
        CourseRating updatedRating = ratingMapper.selectById(testRating.getId());
        assertNotNull(updatedRating, "评价应仍然存在于数据库中");
        assertEquals(ratingDTO.getRating(), updatedRating.getRating(), "数据库中的评价星级应更新");
        assertEquals(ratingDTO.getComment(), updatedRating.getComment(), "数据库中的评价内容应更新");
    }

    @Test
    public void testDeleteRating() {
        // 删除评价
        ratingService.deleteRating(testRating.getId(), studentDetails);
        
        // 验证数据库中的记录是否被删除
        CourseRating deletedRating = ratingMapper.selectById(testRating.getId());
        assertNull(deletedRating, "评价应被删除");
    }

    @Test
    public void testGetRatingById() {
        // 获取评价详情
        CourseRatingVO result = ratingService.getRatingById(testRating.getId());

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertEquals(testRating.getId(), result.getId(), "评价ID应匹配");
        assertEquals(testRating.getRating(), result.getRating(), "评价星级应匹配");
        assertEquals(testRating.getComment(), result.getComment(), "评价内容应匹配");
        assertEquals(testStudent.getId(), result.getStudentId(), "学生ID应匹配");
    }

    @Test
    public void testGetRatingsByCourseId() {
        // 获取课程评价列表
        IPage<CourseRatingVO> results = ratingService.getRatingsByCourseId(testCourse.getId(), 1, 10);

        // 断言
        assertNotNull(results, "返回结果不应为空");
        assertTrue(results.getTotal() > 0, "应该有评价");
        assertEquals(1, results.getRecords().size(), "应该只有一个评价");
        assertEquals(testRating.getId(), results.getRecords().get(0).getId(), "评价ID应匹配");
    }

    @Test
    public void testGetAverageRating() {
        // 先添加另一个评价
        CourseRating anotherRating = new CourseRating();
        anotherRating.setCourseId(testCourse.getId());
        anotherRating.setStudentId(testStudent.getId() + 1);  // 不同学生
        anotherRating.setRating(2);  // 2星评价
        anotherRating.setComment("另一个评价");
        anotherRating.setIsAnonymous(false);
        anotherRating.setCreatedAt(LocalDateTime.now());
        anotherRating.setUpdatedAt(LocalDateTime.now());
        anotherRating.setIsDeleted(false);
        ratingMapper.insert(anotherRating);

        // 获取平均评分
        Double avgRating = ratingService.getAverageRating(testCourse.getId());

        // 断言 - 平均评分应为3.0（(4+2)/2）
        assertNotNull(avgRating, "返回结果不应为空");
        assertEquals(3.0, avgRating, 0.01, "平均评分应为3.0");
    }

    @Test
    public void testHasRated() {
        // 检查学生是否已评价
        boolean hasRated = ratingService.hasRated(testCourse.getId(), studentDetails);

        // 断言
        assertTrue(hasRated, "学生应已评价");

        // 删除评价
        ratingMapper.deleteById(testRating.getId());

        // 再次检查
        hasRated = ratingService.hasRated(testCourse.getId(), studentDetails);

        // 断言
        assertFalse(hasRated, "学生应未评价");
    }

    @Test
    public void testSubmitRatingToUncompletedCourse() {
        // 修改课程状态为进行中
        testCourse.setStatus("in_progress");
        courseMapper.updateById(testCourse);

        // 删除已有评价
        ratingMapper.deleteById(testRating.getId());

        // 创建评价DTO
        CourseRatingDTO ratingDTO = new CourseRatingDTO();
        ratingDTO.setCourseId(testCourse.getId());
        ratingDTO.setRating(5);
        ratingDTO.setComment("这是一个很好的课程");
        ratingDTO.setIsAnonymous(false);

        // 断言抛出异常
        assertThrows(BusinessException.class, () -> {
            ratingService.submitRating(ratingDTO, studentDetails);
        }, "应该抛出BusinessException异常");
    }
} 