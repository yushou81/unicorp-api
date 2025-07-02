package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.dto.CourseQuestionDTO;
import com.csu.unicorp.entity.CourseChapter;
import com.csu.unicorp.entity.CourseQuestion;
import com.csu.unicorp.entity.DualTeacherCourse;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.CourseChapterMapper;
import com.csu.unicorp.mapper.CourseQuestionMapper;
import com.csu.unicorp.mapper.DualTeacherCourseMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.CourseQuestionService;
import com.csu.unicorp.vo.CourseQuestionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 课程问答服务实现类
 */
@Service
@RequiredArgsConstructor
public class CourseQuestionServiceImpl implements CourseQuestionService {

    private final CourseQuestionMapper questionMapper;
    private final CourseChapterMapper chapterMapper;
    private final DualTeacherCourseMapper courseMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public CourseQuestionVO askQuestion(CourseQuestionDTO questionDTO, UserDetails userDetails) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(questionDTO.getCourseId());
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("课程不存在");
        }

        // 如果指定了章节，验证章节是否存在
        if (questionDTO.getChapterId() != null) {
            CourseChapter chapter = chapterMapper.selectById(questionDTO.getChapterId());
            if (chapter == null || Boolean.TRUE.equals(chapter.getIsDeleted())) {
                throw new RuntimeException("章节不存在");
            }
            
            // 验证章节是否属于该课程
            if (!chapter.getCourseId().equals(questionDTO.getCourseId())) {
                throw new RuntimeException("章节不属于该课程");
            }
        }

        // 获取当前用户ID
        Integer userId = Integer.parseInt(userDetails.getUsername());

        // 创建问题
        CourseQuestion question = new CourseQuestion();
        question.setCourseId(questionDTO.getCourseId());
        question.setChapterId(questionDTO.getChapterId());
        question.setStudentId(userId);
        question.setTitle(questionDTO.getTitle());
        question.setContent(questionDTO.getContent());
        question.setStatus("pending");
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());
        question.setIsDeleted(false);

        questionMapper.insert(question);

        return convertToVO(question);
    }

    @Override
    @Transactional
    public CourseQuestionVO answerQuestion(Integer questionId, String answer, UserDetails userDetails) {
        // 验证问题是否存在
        CourseQuestion question = questionMapper.selectById(questionId);
        if (question == null || Boolean.TRUE.equals(question.getIsDeleted())) {
            throw new RuntimeException("问题不存在");
        }

        // 获取当前用户ID和角色
        Integer userId = Integer.parseInt(userDetails.getUsername());
        boolean isTeacher = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"));
        boolean isAdmin = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isMentor = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MENTOR"));

        // 验证是否有权限回答
        if (!isTeacher && !isAdmin && !isMentor) {
            throw new RuntimeException("无权回答问题");
        }

        // 更新问题
        question.setAnswer(answer);
        question.setAnsweredBy(userId);
        question.setAnsweredAt(LocalDateTime.now());
        question.setStatus("answered");
        question.setUpdatedAt(LocalDateTime.now());

        questionMapper.updateById(question);

        return convertToVO(question);
    }

    @Override
    public CourseQuestionVO getQuestionDetail(Integer questionId, UserDetails userDetails) {
        // 验证问题是否存在
        CourseQuestion question = questionMapper.selectById(questionId);
        if (question == null || Boolean.TRUE.equals(question.getIsDeleted())) {
            throw new RuntimeException("问题不存在");
        }

        return convertToVO(question);
    }

    @Override
    public IPage<CourseQuestionVO> getCourseQuestions(Integer courseId, Integer page, Integer size, UserDetails userDetails) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("课程不存在");
        }

        // 分页查询课程问题
        Page<CourseQuestion> pageParam = new Page<>(page, size);
        IPage<CourseQuestion> questionPage = questionMapper.selectQuestionsByCourse(courseId, pageParam);
        
        // 转换为VO
        return questionPage.convert(this::convertToVO);
    }

    @Override
    public IPage<CourseQuestionVO> getChapterQuestions(Integer chapterId, Integer page, Integer size, UserDetails userDetails) {
        // 验证章节是否存在
        CourseChapter chapter = chapterMapper.selectById(chapterId);
        if (chapter == null || Boolean.TRUE.equals(chapter.getIsDeleted())) {
            throw new RuntimeException("章节不存在");
        }

        // 分页查询章节问题
        Page<CourseQuestion> pageParam = new Page<>(page, size);
        IPage<CourseQuestion> questionPage = questionMapper.selectQuestionsByChapter(chapterId, pageParam);
        
        // 转换为VO
        return questionPage.convert(this::convertToVO);
    }

    @Override
    public IPage<CourseQuestionVO> getStudentQuestions(Integer courseId, Integer studentId, Integer page, Integer size, UserDetails userDetails) {
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

        // 分页查询学生问题
        Page<CourseQuestion> pageParam = new Page<>(page, size);
        IPage<CourseQuestion> questionPage = questionMapper.selectQuestionsByStudentAndCourse(pageParam, studentId, courseId);
        
        // 转换为VO
        return questionPage.convert(this::convertToVO);
    }

    @Override
    @Transactional
    public CourseQuestionVO updateQuestion(Integer questionId, String title, String content, UserDetails userDetails) {
        // 验证问题是否存在
        CourseQuestion question = questionMapper.selectById(questionId);
        if (question == null || Boolean.TRUE.equals(question.getIsDeleted())) {
            throw new RuntimeException("问题不存在");
        }

        // 获取当前用户ID
        Integer userId = Integer.parseInt(userDetails.getUsername());

        // 验证是否是问题提问者
        if (!question.getStudentId().equals(userId)) {
            throw new RuntimeException("无权修改他人的问题");
        }

        // 验证问题是否已回答
        if ("answered".equals(question.getStatus())) {
            throw new RuntimeException("已回答的问题不能修改");
        }

        // 更新问题
        question.setTitle(title);
        question.setContent(content);
        question.setUpdatedAt(LocalDateTime.now());

        questionMapper.updateById(question);

        return convertToVO(question);
    }

    @Override
    @Transactional
    public boolean deleteQuestion(Integer questionId, UserDetails userDetails) {
        // 验证问题是否存在
        CourseQuestion question = questionMapper.selectById(questionId);
        if (question == null || Boolean.TRUE.equals(question.getIsDeleted())) {
            throw new RuntimeException("问题不存在");
        }

        // 获取当前用户ID和角色
        Integer userId = Integer.parseInt(userDetails.getUsername());
        boolean isAdmin = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isTeacher = userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_TEACHER"));

        // 验证是否有权限删除
        if (!question.getStudentId().equals(userId) && !isAdmin && !isTeacher) {
            throw new RuntimeException("无权删除他人的问题");
        }

        // 逻辑删除问题
        question.setIsDeleted(true);
        questionMapper.updateById(question);

        return true;
    }

    @Override
    public Map<String, Object> getQuestionStatistics(Integer courseId) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("课程不存在");
        }

        // 获取问答统计数据
        Map<String, Object> result = new HashMap<>();
        
        // 获取问题总数
        Integer totalQuestions = questionMapper.countQuestionsByCourse(courseId);
        result.put("totalQuestions", totalQuestions);
        
        // 获取已回答问题数
        Integer answeredQuestions = questionMapper.countAnsweredQuestionsByCourse(courseId);
        result.put("answeredQuestions", answeredQuestions);
        
        // 获取未回答问题数
        Integer pendingQuestions = questionMapper.countPendingQuestionsByCourse(courseId);
        result.put("pendingQuestions", pendingQuestions);
        
        // 计算回答率
        double answerRate = totalQuestions == 0 ? 0 : (double) answeredQuestions / totalQuestions * 100;
        result.put("answerRate", Math.round(answerRate));
        
        return result;
    }

    @Override
    public Integer countPendingQuestionsForTeacher(Integer courseId, Integer teacherId) {
        // 验证课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null || Boolean.TRUE.equals(course.getIsDeleted())) {
            throw new RuntimeException("课程不存在");
        }

        // 验证教师是否是课程的教师
        if (!teacherId.equals(course.getTeacherId()) && !teacherId.equals(course.getMentorId())) {
            throw new RuntimeException("教师不是该课程的授课教师或导师");
        }

        // 统计待回答问题数
        return questionMapper.countPendingQuestionsByCourse(courseId);
    }
    
    /**
     * 将实体转换为VO
     * @param question 问题实体
     * @return 问题VO
     */
    private CourseQuestionVO convertToVO(CourseQuestion question) {
        CourseQuestionVO vo = new CourseQuestionVO();
        BeanUtils.copyProperties(question, vo);
        
        // 设置提问者信息
        User student = userMapper.selectById(question.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getNickname());
        }
        
        // 设置回答者信息
        if (question.getAnsweredBy() != null) {
            User answerer = userMapper.selectById(question.getAnsweredBy());
            if (answerer != null) {
                vo.setAnsweredByName(answerer.getNickname());
            }
        }
        
        return vo;
    }
} 