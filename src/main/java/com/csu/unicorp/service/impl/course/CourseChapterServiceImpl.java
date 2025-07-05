package com.csu.unicorp.service.impl.course;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.CourseChapterDTO;
import com.csu.unicorp.entity.ChapterResource;
import com.csu.unicorp.entity.course.CourseChapter;
import com.csu.unicorp.entity.course.CourseResource;
import com.csu.unicorp.entity.DualTeacherCourse;
import com.csu.unicorp.common.exception.ResourceNotFoundException;
import com.csu.unicorp.vo.CourseResourceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import com.csu.unicorp.mapper.ChapterResourceMapper;
import com.csu.unicorp.mapper.course.CourseChapterMapper;
import com.csu.unicorp.mapper.course.CourseResourceMapper;
import com.csu.unicorp.mapper.course.DualTeacherCourseMapper;
import com.csu.unicorp.service.CourseChapterService;
import com.csu.unicorp.vo.CourseChapterVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程章节服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseChapterServiceImpl implements CourseChapterService {

    private final CourseChapterMapper chapterMapper;
    private final ChapterResourceMapper chapterResourceMapper;
    private final CourseResourceMapper resourceMapper;
    private final DualTeacherCourseMapper courseMapper;

    @Override
    @Transactional
    public CourseChapterVO createChapter(CourseChapterDTO chapterDTO, UserDetails userDetails) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(chapterDTO.getCourseId());
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new ResourceNotFoundException("课程不存在");
        }

        // 检查权限
        checkCoursePermission(course, userDetails);

        // 获取最大序号
        Integer maxSequence = chapterMapper.selectMaxSequenceByCourseId(chapterDTO.getCourseId());
        Integer sequence = maxSequence == null ? 1 : maxSequence + 1;

        // 创建章节
        CourseChapter chapter = new CourseChapter();
        BeanUtils.copyProperties(chapterDTO, chapter);
        chapter.setSequence(sequence);
        chapter.setIsPublished(false);
        chapter.setCreatedAt(LocalDateTime.now());
        chapter.setUpdatedAt(LocalDateTime.now());
        chapter.setIsDeleted(false);

        chapterMapper.insert(chapter);

        return convertToVO(chapter);
    }

    @Override
    @Transactional
    public CourseChapterVO updateChapter(Integer chapterId, CourseChapterDTO chapterDTO, UserDetails userDetails) {
        // 验证章节是否存在
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null || Boolean.TRUE.equals(chapter.getIsDeleted())) {
            throw new ResourceNotFoundException("章节不存在");
        }

        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(chapter.getCourseId());
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new ResourceNotFoundException("课程不存在");
        }

        // 检查权限
        checkCoursePermission(course, userDetails);

        // 更新章节
        chapter.setTitle(chapterDTO.getTitle());
        chapter.setDescription(chapterDTO.getDescription());
        chapter.setUpdatedAt(LocalDateTime.now());

        chapterMapper.updateById(chapter);

        return convertToVO(chapter);
    }

    @Override
    @Transactional
    public boolean deleteChapter(Integer chapterId, UserDetails userDetails) {
        // 验证章节是否存在
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null || Boolean.TRUE.equals(chapter.getIsDeleted())) {
            throw new ResourceNotFoundException("章节不存在");
        }

        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(chapter.getCourseId());
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new ResourceNotFoundException("课程不存在");
        }

        // 检查权限
        checkCoursePermission(course, userDetails);

        // 使用MyBatis-Plus的deleteById方法进行逻辑删除
        return chapterMapper.deleteById(chapterId) > 0;
    }

    @Override
    public CourseChapterVO getChapterDetail(Integer chapterId, UserDetails userDetails) {
        // 验证章节是否存在
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null || Boolean.TRUE.equals(chapter.getIsDeleted())) {
            throw new ResourceNotFoundException("章节不存在");
        }

        return convertToVO(chapter);
    }

    @Override
    public List<CourseChapterVO> getChaptersByCourse(Integer courseId, UserDetails userDetails) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new ResourceNotFoundException("课程不存在");
        }

        // 获取章节列表
        List<CourseChapter> chapters;
        
        // 如果是教师或管理员，返回所有章节
        if (hasTeacherOrAdminRole(userDetails)) {
            chapters = chapterMapper.selectChaptersByCourseId(courseId);
        } else {
            // 否则只返回已发布的章节
            chapters = chapterMapper.selectPublishedChaptersByCourseId(courseId);
        }

        return chapters.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseChapterVO updateChapterPublishStatus(Integer chapterId, Boolean isPublished, UserDetails userDetails) {
        // 验证章节是否存在
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null || Boolean.TRUE.equals(chapter.getIsDeleted())) {
            throw new ResourceNotFoundException("章节不存在");
        }

        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(chapter.getCourseId());
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new ResourceNotFoundException("课程不存在");
        }

        // 检查权限
        checkCoursePermission(course, userDetails);

        // 更新发布状态
        chapter.setIsPublished(isPublished);
        chapter.setUpdatedAt(LocalDateTime.now());
        chapterMapper.updateById(chapter);

        return convertToVO(chapter);
    }

    @Override
    @Transactional
    public boolean updateChapterSequence(Integer chapterId, Integer sequence, UserDetails userDetails) {
        // 验证章节是否存在
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null || Boolean.TRUE.equals(chapter.getIsDeleted())) {
            throw new ResourceNotFoundException("章节不存在");
        }

        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(chapter.getCourseId());
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new ResourceNotFoundException("课程不存在");
        }

        // 检查权限
        checkCoursePermission(course, userDetails);

        // 更新序号
        chapterMapper.updateChapterSequence(chapterId, sequence);

        return true;
    }

    @Override
    @Transactional
    public boolean associateResourceToChapter(Integer chapterId, Integer resourceId, UserDetails userDetails) {
        // 验证章节是否存在
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null || Boolean.TRUE.equals(chapter.getIsDeleted())) {
            throw new ResourceNotFoundException("章节不存在");
        }

        // 验证资源是否存在
        CourseResource resource = resourceMapper.selectById(resourceId);
        if (resource == null || Boolean.TRUE.equals(resource.getIsDeleted())) {
            throw new ResourceNotFoundException("资源不存在");
        }

        // 验证资源和章节属于同一课程
        if (!resource.getCourseId().equals(chapter.getCourseId())) {
            throw new IllegalArgumentException("资源不属于该课程");
        }

        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(chapter.getCourseId());
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new ResourceNotFoundException("课程不存在");
        }

        // 检查权限
        checkCoursePermission(course, userDetails);

        // 检查是否已关联
        LambdaQueryWrapper<ChapterResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChapterResource::getChapterId, chapterId)
                .eq(ChapterResource::getResourceId, resourceId)
                .eq(ChapterResource::getIsDeleted, false);
        
        if (chapterResourceMapper.selectCount(wrapper) > 0) {
            return true; // 已关联，直接返回成功
        }

        // 获取最大序号
        Integer maxSequence = chapterResourceMapper.selectMaxSequenceByChapterId(chapterId);
        Integer sequence = maxSequence == null ? 1 : maxSequence + 1;

        // 创建关联
        ChapterResource chapterResource = new ChapterResource();
        chapterResource.setChapterId(chapterId);
        chapterResource.setResourceId(resourceId);
        chapterResource.setSequence(sequence);
        chapterResource.setCreatedAt(LocalDateTime.now());
        chapterResource.setIsDeleted(false);

        chapterResourceMapper.insert(chapterResource);

        return true;
    }

    @Override
    @Transactional
    public boolean removeResourceFromChapter(Integer chapterId, Integer resourceId, UserDetails userDetails) {
        // 验证章节是否存在
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null || Boolean.TRUE.equals(chapter.getIsDeleted())) {
            throw new ResourceNotFoundException("章节不存在");
        }

        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(chapter.getCourseId());
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new ResourceNotFoundException("课程不存在");
        }

        // 检查权限
        checkCoursePermission(course, userDetails);

        // 查找关联记录
        LambdaQueryWrapper<ChapterResource> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChapterResource::getChapterId, chapterId)
                .eq(ChapterResource::getResourceId, resourceId)
                .eq(ChapterResource::getIsDeleted, false);
        
        ChapterResource chapterResource = chapterResourceMapper.selectOne(wrapper);
        if (chapterResource == null) {
            throw new ResourceNotFoundException("资源未关联到该章节");
        }

        // 逻辑删除关联
        chapterResourceMapper.deleteById(chapterResource);

        return true;
    }

    @Override
    public List<Integer> getChapterResources(Integer chapterId, UserDetails userDetails) {
        // 验证章节是否存在
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null || Boolean.TRUE.equals(chapter.getIsDeleted())) {
            throw new ResourceNotFoundException("章节不存在");
        }

        // 获取资源ID列表
        return chapterResourceMapper.selectResourceIdsByChapterId(chapterId);
    }

    /**
     * 检查用户是否有权限操作课程
     * @param course 课程
     * @param userDetails 用户
     */
    private void checkCoursePermission(DualTeacherCourse course, UserDetails userDetails) {
        // 如果是管理员，直接通过
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SYSADMIN"))) {
            return;
        }
        log.info("checkCoursePermission: userDetails = {}", userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER")));
        // 如果是教师，检查是否是课程的教师
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"))) {
            Integer userId = ((CustomUserDetails) userDetails).getUserId();
            if (course.getTeacherId() != null && course.getTeacherId().equals(userId)) {
                return;
            }
        }

        throw new AccessDeniedException("无权操作该课程");
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
     * @param chapter 章节实体
     * @return 章节VO
     */
    private CourseChapterVO convertToVO(CourseChapter chapter) {
        CourseChapterVO vo = new CourseChapterVO();
        BeanUtils.copyProperties(chapter, vo);
        
        // 查询课程标题
        DualTeacherCourse course = courseMapper.selectById(chapter.getCourseId());
        if (course != null) {
            vo.setCourseTitle(course.getTitle());
        }
        
        // 查询完成学生数量和总学生数量
        Integer completedCount = chapterMapper.countStudentsCompletedChapter(chapter.getId());
        Integer totalStudentCount = chapterMapper.countStudentsInCourse(chapter.getCourseId());
        
        vo.setCompletedCount(completedCount != null ? completedCount : 0);
        vo.setTotalStudentCount(totalStudentCount != null ? totalStudentCount : 0);
        
        // 查询关联的资源列表
        List<Integer> resourceIds = chapterResourceMapper.selectResourceIdsByChapterId(chapter.getId());
        if (resourceIds != null && !resourceIds.isEmpty()) {
            List<CourseResource> resources = resourceMapper.selectBatchIds(resourceIds);
            List<CourseResourceVO> resourceVOs = new ArrayList<>();
            
            for (CourseResource resource : resources) {
                CourseResourceVO resourceVO = new CourseResourceVO();
                BeanUtils.copyProperties(resource, resourceVO);
                // 可以在这里添加其他需要的资源信息
                resourceVOs.add(resourceVO);
            }
            
            vo.setResources(resourceVOs);
        }
        
        return vo;
    }
} 