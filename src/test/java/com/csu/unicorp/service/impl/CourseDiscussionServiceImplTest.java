package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.CourseDiscussionDTO;
import com.csu.unicorp.entity.course.CourseDiscussion;
import com.csu.unicorp.entity.DualTeacherCourse;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.CourseDiscussionMapper;
import com.csu.unicorp.mapper.DualTeacherCourseMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.vo.CourseDiscussionVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest
@ActiveProfiles("test")
@Transactional  // 使用事务管理，测试后回滚
public class CourseDiscussionServiceImplTest {

    @Autowired
    private CourseDiscussionServiceImpl discussionService;

    @Autowired
    private CourseDiscussionMapper discussionMapper;

    @Autowired
    private DualTeacherCourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    private DualTeacherCourse testCourse;
    private User testStudent;
    private User testTeacher;
    private CourseDiscussion testDiscussion;
    private UserDetails studentDetails;
    private UserDetails teacherDetails;

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

        // 创建测试讨论
        testDiscussion = new CourseDiscussion();
        testDiscussion.setCourseId(testCourse.getId());
        testDiscussion.setUserId(testStudent.getId());
        testDiscussion.setContent("这是一个测试讨论");
        testDiscussion.setParentId(null);
        testDiscussion.setCreatedAt(LocalDateTime.now());
        testDiscussion.setUpdatedAt(LocalDateTime.now());
        testDiscussion.setIsDeleted(false);
        discussionMapper.insert(testDiscussion);

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

        // 创建教师用户详情
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
    public void testCreateDiscussion() {
        // 创建讨论DTO
        CourseDiscussionDTO discussionDTO = new CourseDiscussionDTO();
        discussionDTO.setCourseId(testCourse.getId());
        discussionDTO.setContent("这是一个新讨论");

        // 创建讨论
        CourseDiscussionVO result = discussionService.createDiscussion(discussionDTO, studentDetails);

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertEquals(discussionDTO.getContent(), result.getContent(), "讨论内容应匹配");
        assertEquals(testStudent.getId(), result.getUserId(), "用户ID应匹配");
        assertEquals(testStudent.getNickname(), result.getUserName(), "用户名应匹配");
    }

    @Test
    public void testReplyToDiscussion() {
        // 创建回复DTO
        CourseDiscussionDTO replyDTO = new CourseDiscussionDTO();
        replyDTO.setParentId(testDiscussion.getId());
        replyDTO.setContent("这是一个回复");

        // 回复讨论
        CourseDiscussionVO result = discussionService.replyToDiscussion(replyDTO, studentDetails);

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertEquals(replyDTO.getContent(), result.getContent(), "回复内容应匹配");
        assertEquals(testStudent.getId(), result.getUserId(), "用户ID应匹配");
        assertEquals(testDiscussion.getId(), result.getParentId(), "父讨论ID应匹配");
    }

    @Test
    public void testGetDiscussionDetail() {
        // 获取讨论详情
        CourseDiscussionVO result = discussionService.getDiscussionDetail(testDiscussion.getId(), studentDetails);

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertEquals(testDiscussion.getId(), result.getId(), "讨论ID应匹配");
        assertEquals(testDiscussion.getContent(), result.getContent(), "讨论内容应匹配");
        assertEquals(testStudent.getId(), result.getUserId(), "用户ID应匹配");
        assertEquals(testStudent.getNickname(), result.getUserName(), "用户名应匹配");
    }

    @Test
    public void testGetCourseDiscussions() {
        // 获取课程讨论列表
        IPage<CourseDiscussionVO> results = discussionService.getCourseDiscussions(testCourse.getId(), 0, 10, studentDetails);

        // 断言
        assertNotNull(results, "返回结果不应为空");
        assertTrue(results.getTotal() > 0, "应该有讨论");
        assertEquals(1, results.getRecords().size(), "应该只有一个讨论");
        assertEquals(testDiscussion.getId(), results.getRecords().get(0).getId(), "讨论ID应匹配");
    }

    @Test
    public void testUpdateDiscussion() {
        // 更新讨论
        String updatedContent = "更新后的讨论内容";
        CourseDiscussionVO result = discussionService.updateDiscussion(testDiscussion.getId(), updatedContent, studentDetails);

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertEquals(updatedContent, result.getContent(), "讨论内容应更新");
        
        // 验证数据库中的记录是否更新
        CourseDiscussion updatedDiscussion = discussionMapper.selectById(testDiscussion.getId());
        assertNotNull(updatedDiscussion, "讨论应仍然存在于数据库中");
        assertEquals(updatedContent, updatedDiscussion.getContent(), "数据库中的讨论内容应更新");
    }

    @Test
    public void testDeleteDiscussionByTeacher() {
        // 教师删除讨论
        boolean result = discussionService.deleteDiscussion(testDiscussion.getId(), teacherDetails);

        // 断言
        assertTrue(result, "删除讨论应成功");
        
        // 验证数据库中的记录是否被标记为删除
        CourseDiscussion deletedDiscussion = discussionMapper.selectById(testDiscussion.getId());
        assertNotNull(deletedDiscussion, "讨论应该仍然存在于数据库中");
        assertTrue(deletedDiscussion.getIsDeleted(), "讨论应该被标记为删除");
    }

    @Test
    public void testGetDiscussionReplies() {
        // 先创建一个回复
        CourseDiscussion reply = new CourseDiscussion();
        reply.setCourseId(testCourse.getId());
        reply.setUserId(testStudent.getId());
        reply.setContent("这是一个测试回复");
        reply.setParentId(testDiscussion.getId());
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUpdatedAt(LocalDateTime.now());
        reply.setIsDeleted(false);
        discussionMapper.insert(reply);

        // 获取讨论回复
        List<CourseDiscussionVO> results = discussionService.getDiscussionReplies(testDiscussion.getId(), studentDetails);

        // 断言
        assertNotNull(results, "返回结果不应为空");
        assertEquals(1, results.size(), "应该只有一个回复");
        assertEquals(reply.getId(), results.get(0).getId(), "回复ID应匹配");
        assertEquals(reply.getContent(), results.get(0).getContent(), "回复内容应匹配");
    }

    @Test
    public void testCountCourseDiscussions() {
        // 统计课程讨论数量
        Integer count = discussionService.countCourseDiscussions(testCourse.getId());

        // 断言
        assertNotNull(count, "返回结果不应为空");
        assertEquals(1, count, "讨论数量应为1");

        // 添加一个回复
        CourseDiscussion reply = new CourseDiscussion();
        reply.setCourseId(testCourse.getId());
        reply.setUserId(testStudent.getId());
        reply.setContent("这是一个测试回复");
        reply.setParentId(testDiscussion.getId());
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUpdatedAt(LocalDateTime.now());
        reply.setIsDeleted(false);
        discussionMapper.insert(reply);

        // 再次统计
        count = discussionService.countCourseDiscussions(testCourse.getId());

        // 断言
        assertEquals(2, count, "讨论数量应为2");
    }
} 