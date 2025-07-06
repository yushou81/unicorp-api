package com.csu.unicorp.service.impl;

import com.csu.unicorp.dto.CourseChapterDTO;
import com.csu.unicorp.entity.course.CourseChapter;
import com.csu.unicorp.entity.course.CourseResource;
import com.csu.unicorp.entity.DualTeacherCourse;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.common.exception.ResourceNotFoundException;
import com.csu.unicorp.mapper.course.CourseChapterMapper;
import com.csu.unicorp.mapper.course.CourseResourceMapper;
import com.csu.unicorp.mapper.course.DualTeacherCourseMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.impl.course.CourseChapterServiceImpl;
import com.csu.unicorp.vo.CourseChapterVO;
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
public class CourseChapterServiceImplTest {

    @Autowired
    private CourseChapterServiceImpl courseChapterService;

    @Autowired
    private CourseChapterMapper chapterMapper;

    @Autowired
    private DualTeacherCourseMapper courseMapper;

    @Autowired
    private CourseResourceMapper resourceMapper;

    @Autowired
    private UserMapper userMapper;

    private DualTeacherCourse testCourse;
    private User testTeacher;
    private CourseChapter testChapter;
    private CourseResource testResource;
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

        // 创建测试章节
        testChapter = new CourseChapter();
        testChapter.setCourseId(testCourse.getId());
        testChapter.setTitle("测试章节");
        testChapter.setDescription("这是一个测试章节");
        testChapter.setSequence(1);
        testChapter.setIsPublished(true);
        testChapter.setCreatedAt(LocalDateTime.now());
        testChapter.setUpdatedAt(LocalDateTime.now());
        testChapter.setIsDeleted(false);
        chapterMapper.insert(testChapter);

        // 创建测试资源
        testResource = new CourseResource();
        testResource.setCourseId(testCourse.getId());
        testResource.setTitle("测试资源");
        testResource.setDescription("这是一个测试资源");
        testResource.setResourceType("document");
        testResource.setFilePath("/path/to/resource.pdf");
        testResource.setFileSize(1024L);
        testResource.setFileType("application/pdf");
        testResource.setUploaderId(testTeacher.getId());
        testResource.setUploaderType("TEACHER");
        testResource.setDownloadCount(0);
        testResource.setCreatedAt(LocalDateTime.now());
        testResource.setUpdatedAt(LocalDateTime.now());
        testResource.setIsDeleted(false);
        resourceMapper.insert(testResource);

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
    public void testCreateChapter() {
        // 创建章节DTO
        CourseChapterDTO chapterDTO = new CourseChapterDTO();
        chapterDTO.setCourseId(testCourse.getId());
        chapterDTO.setTitle("新章节");
        chapterDTO.setDescription("这是一个新章节");

        // 创建章节
        CourseChapterVO result = courseChapterService.createChapter(chapterDTO, teacherDetails);

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertEquals("新章节", result.getTitle(), "章节标题应匹配");
        assertEquals("这是一个新章节", result.getDescription(), "章节描述应匹配");
        assertEquals(testCourse.getId(), result.getCourseId(), "课程ID应匹配");
        assertFalse(result.getIsPublished(), "新章节默认应为未发布状态");
        
        // 验证数据库中是否有记录
        CourseChapter savedChapter = chapterMapper.selectById(result.getId());
        assertNotNull(savedChapter, "应该在数据库中存在新章节");
        assertEquals("新章节", savedChapter.getTitle(), "数据库中的章节标题应匹配");
    }

    @Test
    public void testUpdateChapter() {
        // 创建更新章节DTO
        CourseChapterDTO chapterDTO = new CourseChapterDTO();
        chapterDTO.setTitle("更新的章节标题");
        chapterDTO.setDescription("更新的章节描述");

        // 更新章节
        CourseChapterVO result = courseChapterService.updateChapter(testChapter.getId(), chapterDTO, teacherDetails);

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertEquals("更新的章节标题", result.getTitle(), "章节标题应更新");
        assertEquals("更新的章节描述", result.getDescription(), "章节描述应更新");
        
        // 验证数据库中的记录是否更新
        CourseChapter updatedChapter = chapterMapper.selectById(testChapter.getId());
        assertNotNull(updatedChapter, "章节应仍然存在于数据库中");
        assertEquals("更新的章节标题", updatedChapter.getTitle(), "数据库中的章节标题应更新");
    }

    @Test
    public void testDeleteChapter() {
        // 删除章节
        boolean result = courseChapterService.deleteChapter(testChapter.getId(), teacherDetails);

        // 断言
        assertTrue(result, "删除章节应成功");
        
        // 验证数据库中的记录是否被标记为删除
        CourseChapter deletedChapter = chapterMapper.selectById(testChapter.getId());
        assertNull(deletedChapter, "章节应该被逻辑删除");
    }

    @Test
    public void testGetChapterDetail() {
        // 获取章节详情
        CourseChapterVO result = courseChapterService.getChapterDetail(testChapter.getId(), teacherDetails);

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertEquals(testChapter.getId(), result.getId(), "章节ID应匹配");
        assertEquals(testChapter.getTitle(), result.getTitle(), "章节标题应匹配");
        assertEquals(testChapter.getDescription(), result.getDescription(), "章节描述应匹配");
    }

    @Test
    public void testGetChaptersByCourse() {
        // 获取课程章节列表
        List<CourseChapterVO> results = courseChapterService.getChaptersByCourse(testCourse.getId(), teacherDetails);

        // 断言
        assertNotNull(results, "返回结果不应为空");
        assertFalse(results.isEmpty(), "应该有章节");
        assertEquals(1, results.size(), "应该只有一个章节");
        assertEquals(testChapter.getId(), results.get(0).getId(), "章节ID应匹配");
    }

    @Test
    public void testUpdateChapterPublishStatus() {
        // 更新章节发布状态为未发布
        CourseChapterVO result = courseChapterService.updateChapterPublishStatus(testChapter.getId(), false, teacherDetails);

        // 断言
        assertNotNull(result, "返回结果不应为空");
        assertFalse(result.getIsPublished(), "章节应为未发布状态");
        
        // 验证数据库中的记录是否更新
        CourseChapter updatedChapter = chapterMapper.selectById(testChapter.getId());
        assertNotNull(updatedChapter, "章节应仍然存在于数据库中");
        assertFalse(updatedChapter.getIsPublished(), "数据库中的章节发布状态应更新");
    }

    @Test
    public void testAssociateResourceToChapter() {
        // 关联资源到章节
        boolean result = courseChapterService.associateResourceToChapter(testChapter.getId(), testResource.getId(), teacherDetails);

        // 断言
        assertTrue(result, "关联资源到章节应成功");
        
        // 验证章节资源关联
        List<Integer> resourceIds = courseChapterService.getChapterResources(testChapter.getId(), teacherDetails);
        assertNotNull(resourceIds, "返回结果不应为空");
        assertFalse(resourceIds.isEmpty(), "应该有关联的资源");
        assertTrue(resourceIds.contains(testResource.getId()), "资源ID应存在于关联列表中");
    }

    @Test
    public void testResourceNotFound() {
        // 测试当资源不存在时的异常
        CourseChapterDTO chapterDTO = new CourseChapterDTO();
        chapterDTO.setCourseId(9999);  // 不存在的课程ID
        chapterDTO.setTitle("新章节");
        chapterDTO.setDescription("这是一个新章节");

        // 断言抛出ResourceNotFoundException异常
        assertThrows(ResourceNotFoundException.class, () -> {
            courseChapterService.createChapter(chapterDTO, teacherDetails);
        }, "应该抛出ResourceNotFoundException异常");
    }
} 