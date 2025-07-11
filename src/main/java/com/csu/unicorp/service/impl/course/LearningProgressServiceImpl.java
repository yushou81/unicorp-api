package com.csu.unicorp.service.impl.course;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.exception.LearningProgressException;
import com.csu.unicorp.config.security.CustomUserDetails;
import com.csu.unicorp.dto.LearningProgressDTO;
import com.csu.unicorp.entity.course.CourseChapter;
import com.csu.unicorp.entity.course.CourseEnrollment;
import com.csu.unicorp.entity.DualTeacherCourse;
import com.csu.unicorp.entity.course.LearningProgress;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.course.CourseChapterMapper;
import com.csu.unicorp.mapper.course.CourseEnrollmentMapper;
import com.csu.unicorp.mapper.course.DualTeacherCourseMapper;
import com.csu.unicorp.mapper.course.LearningProgressMapper;
import com.csu.unicorp.service.LearningProgressService;
import com.csu.unicorp.service.UserService;
import com.csu.unicorp.vo.LearningProgressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 学习进度服务实现类
 */
@Service
@RequiredArgsConstructor
public class LearningProgressServiceImpl implements LearningProgressService {

    private final LearningProgressMapper progressMapper;
    private final CourseChapterMapper chapterMapper;
    private final DualTeacherCourseMapper courseMapper;
    private final CourseEnrollmentMapper enrollmentMapper;
    private final UserService userService;

    @Override
    @Transactional
    public LearningProgressVO updateProgress(LearningProgressDTO progressDTO, UserDetails userDetails) {
        // 验证章节是否存在
        CourseChapter chapter = chapterMapper.selectById(progressDTO.getChapterId());
        if (chapter == null || Boolean.TRUE.equals(chapter.getIsDeleted())) {
            throw new LearningProgressException("章节不存在");
        }

        // 获取当前用户ID
        Integer userId = ((CustomUserDetails) userDetails).getUserId();

        // 查询是否已有进度记录
        LambdaQueryWrapper<LearningProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningProgress::getStudentId, userId)
                .eq(LearningProgress::getChapterId, progressDTO.getChapterId());
        
        LearningProgress progress = progressMapper.selectOne(wrapper);
        boolean isNew = false;

        if (progress == null) {
            // 创建新进度记录
            progress = new LearningProgress();
            progress.setStudentId(userId);
            progress.setChapterId(progressDTO.getChapterId());
            progress.setStartTime(LocalDateTime.now());
            progress.setCreatedAt(LocalDateTime.now());
            isNew = true;
        }

        // 更新进度
        progress.setStatus(progressDTO.getStatus());
        progress.setProgressPercent(progressDTO.getProgressPercent());
        progress.setDurationMinutes(progressDTO.getDurationMinutes());
        progress.setUpdatedAt(LocalDateTime.now());

        // 如果完成了，记录完成时间
        if ("completed".equals(progressDTO.getStatus())) {
            progress.setCompleteTime(LocalDateTime.now());
        }

        if (isNew) {
            progressMapper.insert(progress);
        } else {
            progressMapper.updateById(progress);
        }

        return convertToVO(progress);
    }

    @Override
    public LearningProgressVO getStudentProgressInChapter(Integer chapterId, Integer studentId, UserDetails userDetails) {
        // 验证章节是否存在
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null || Boolean.TRUE.equals(chapter.getIsDeleted())) {
            throw new LearningProgressException("章节不存在");
        }
        
        // 如果不是教师或管理员，验证学生是否已报名该课程
        if (!hasTeacherOrAdminRole(userDetails)) {
            LambdaQueryWrapper<CourseEnrollment> enrollmentQuery = new LambdaQueryWrapper<>();
            enrollmentQuery.eq(CourseEnrollment::getCourseId, chapter.getCourseId())
                    .eq(CourseEnrollment::getStudentId, studentId)
                    .eq(CourseEnrollment::getStatus, "enrolled")
                    .eq(CourseEnrollment::getIsDeleted, false);
            
            Long enrollmentCount = enrollmentMapper.selectCount(enrollmentQuery);
            if (enrollmentCount == 0) {
                throw new LearningProgressException("学生未报名该课程，无法查看学习进度");
            }
        }

        // 查询进度记录
        LambdaQueryWrapper<LearningProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LearningProgress::getStudentId, studentId)
                .eq(LearningProgress::getChapterId, chapterId);
        
        LearningProgress progress = progressMapper.selectOne(wrapper);
        
        if (progress == null) {
            // 如果没有进度记录，返回默认值
            LearningProgressVO vo = new LearningProgressVO();
            vo.setStudentId(studentId);
            vo.setChapterId(chapterId);
            vo.setStatus("not_started");
            vo.setProgressPercent(0);
            vo.setDurationMinutes(0);
            return vo;
        }

        return convertToVO(progress);
    }

    @Override
    public List<LearningProgressVO> getStudentProgressInCourse(Integer courseId, Integer studentId, UserDetails userDetails) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new LearningProgressException("课程不存在");
        }
        
        // 如果不是教师或管理员，验证学生是否已报名该课程
        if (!hasTeacherOrAdminRole(userDetails)) {
            LambdaQueryWrapper<CourseEnrollment> enrollmentQuery = new LambdaQueryWrapper<>();
            enrollmentQuery.eq(CourseEnrollment::getCourseId, courseId)
                    .eq(CourseEnrollment::getStudentId, studentId)
                    .eq(CourseEnrollment::getStatus, "enrolled")
                    .eq(CourseEnrollment::getIsDeleted, false);
            
            Long enrollmentCount = enrollmentMapper.selectCount(enrollmentQuery);
            if (enrollmentCount == 0) {
                throw new LearningProgressException("学生未报名该课程，无法查看学习进度");
            }
        }

        // 获取课程的所有章节
        List<CourseChapter> chapters = chapterMapper.selectChaptersByCourseId(courseId);
        
        // 如果课程没有章节，直接返回空列表
        if (chapters == null || chapters.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取学生在这些章节中的进度
        List<Integer> chapterIds = chapters.stream().map(CourseChapter::getId).collect(Collectors.toList());
        List<LearningProgress> progressList;
        
        // 使用适当的查询方法获取学习进度
        if (!chapterIds.isEmpty()) {
            progressList = progressMapper.selectProgressByStudentAndChapters(studentId, chapterIds);
        } else {
            // 如果没有章节，返回空列表
            progressList = new ArrayList<>();
        }
        
        // 转换为VO并返回
        return progressList.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public IPage<LearningProgressVO> getChapterStudentProgress(Integer chapterId, Integer page, Integer size, UserDetails userDetails) {
        // 验证章节是否存在
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null || Boolean.TRUE.equals(chapter.getIsDeleted())) {
            throw new LearningProgressException("章节不存在");
        }

        // 分页查询章节的学生进度
        Page<LearningProgress> pageParam = new Page<>(page, size);
        IPage<LearningProgress> progressPage = progressMapper.selectStudentProgressByChapter(pageParam, chapterId);
        
        // 转换为VO
        return progressPage.convert(this::convertToVO);
    }

    @Override
    public Map<String, Object> getCourseProgressOverview(Integer courseId, UserDetails userDetails) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new LearningProgressException("课程不存在");
        }

        // 获取课程统计数据
        Map<String, Object> result = new HashMap<>();
        
        // 获取课程章节数
        Long chapterCountLong = chapterMapper.selectCount(
                new LambdaQueryWrapper<CourseChapter>()
                        .eq(CourseChapter::getCourseId, courseId)
                        .eq(CourseChapter::getIsDeleted, false)
        );
        Integer chapterCount = chapterCountLong.intValue();
        result.put("chapterCount", chapterCount);
        
        // 获取已完成章节的学生数量
        Integer completedCount = progressMapper.countCompletedChaptersByCourse(courseId);
        result.put("completedChapters", completedCount);
        
        // 获取进行中章节的学生数量
        Integer inProgressCount = progressMapper.countInProgressChaptersByCourse(courseId);
        result.put("inProgressChapters", inProgressCount);
        
        // 获取未开始章节的学生数量
        Integer notStartedCount = progressMapper.countNotStartedChaptersByCourse(courseId);
        result.put("notStartedChapters", notStartedCount);
        
        // 获取平均完成率
        Integer avgCompletionRate = progressMapper.getAverageCompletionRateByCourse(courseId);
        result.put("averageCompletionRate", avgCompletionRate);
        
        return result;
    }

    @Override
    public Integer calculateCourseCompletionRate(Integer courseId, Integer studentId, UserDetails userDetails) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new LearningProgressException("课程不存在");
        }

        // 获取课程的所有章节数量
        Long totalChaptersLong = chapterMapper.selectCount(
                new LambdaQueryWrapper<CourseChapter>()
                        .eq(CourseChapter::getCourseId, courseId)
                        .eq(CourseChapter::getIsPublished, true)
                        .eq(CourseChapter::getIsDeleted, false)
        );
        
        Integer totalChapters = totalChaptersLong.intValue();
        
        if (totalChapters == 0) {
            return 0; // 没有章节，完成率为0
        }
        
        // 获取已完成的章节数量
        Integer completedChapters = progressMapper.countCompletedChaptersByStudentAndCourse(studentId, courseId);
        
        // 计算完成率
        return (int) Math.round((double) completedChapters / totalChapters * 100);
    }

    @Override
    @Transactional
    public boolean initializeChapterProgress(Integer chapterId, UserDetails userDetails) {
        // 验证章节是否存在
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null || Boolean.TRUE.equals(chapter.getIsDeleted())) {
            throw new LearningProgressException("章节不存在");
        }

        // 验证用户权限
        if (!hasTeacherOrAdminRole(userDetails)) {
            throw new LearningProgressException("无权操作");
        }

        // 获取课程已报名的学生
        List<CourseEnrollment> enrollments = enrollmentMapper.selectList(
                new LambdaQueryWrapper<CourseEnrollment>()
                        .eq(CourseEnrollment::getCourseId, chapter.getCourseId())
                        .eq(CourseEnrollment::getStatus, "enrolled")
                        .eq(CourseEnrollment::getIsDeleted, false)
        );
        
        // 为每个学生创建初始进度记录
        for (CourseEnrollment enrollment : enrollments) {
            // 检查是否已存在进度记录
            LambdaQueryWrapper<LearningProgress> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LearningProgress::getStudentId, enrollment.getStudentId())
                    .eq(LearningProgress::getChapterId, chapterId);
            
            Long countLong = progressMapper.selectCount(wrapper);
            if (countLong.intValue() == 0) {
                // 创建新进度记录
                LearningProgress progress = new LearningProgress();
                progress.setStudentId(enrollment.getStudentId());
                progress.setChapterId(chapterId);
                progress.setStatus("not_started");
                progress.setProgressPercent(0);
                progress.setDurationMinutes(0);
                progress.setCreatedAt(LocalDateTime.now());
                progress.setUpdatedAt(LocalDateTime.now());
                
                progressMapper.insert(progress);
            }
        }
        
        return true;
    }
    
    /**
     * 判断用户是否有教师或管理员角色
     * @param userDetails 用户
     * @return 是否有教师或管理员角色
     */
    private boolean hasTeacherOrAdminRole(UserDetails userDetails) {
        return userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER")) ||
               userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SCH_ADMIN")) ||
                userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT")) ;
    }
    
    /**
     * 将实体转换为VO
     * @param progress 进度实体
     * @return 进度VO
     */
    private LearningProgressVO convertToVO(LearningProgress progress) {
        LearningProgressVO vo = new LearningProgressVO();
        BeanUtils.copyProperties(progress, vo);
        
        // 获取学生信息
        User student = userService.getById(progress.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getNickname());
        }
        
        // 获取章节信息
        CourseChapter chapter = chapterMapper.selectById(progress.getChapterId());
        if (chapter != null) {
            vo.setChapterTitle(chapter.getTitle());
            vo.setCourseId(chapter.getCourseId());
            
            // 获取课程信息
            DualTeacherCourse course = courseMapper.selectById(chapter.getCourseId());
            if (course != null) {
                vo.setCourseTitle(course.getTitle());
            }
        }
        
        return vo;
    }
} 