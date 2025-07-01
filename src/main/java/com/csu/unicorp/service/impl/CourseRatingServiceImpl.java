package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.dto.CourseRatingDTO;
import com.csu.unicorp.entity.CourseEnrollment;
import com.csu.unicorp.entity.CourseRating;
import com.csu.unicorp.entity.DualTeacherCourse;
import com.csu.unicorp.mapper.CourseEnrollmentMapper;
import com.csu.unicorp.mapper.CourseRatingMapper;
import com.csu.unicorp.mapper.DualTeacherCourseMapper;
import com.csu.unicorp.service.CourseRatingService;
import com.csu.unicorp.vo.CourseRatingVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 课程评价服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CourseRatingServiceImpl extends ServiceImpl<CourseRatingMapper, CourseRating> implements CourseRatingService {

    private final CourseRatingMapper ratingMapper;
    private final DualTeacherCourseMapper courseMapper;
    private final CourseEnrollmentMapper enrollmentMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseRatingVO submitRating(CourseRatingDTO ratingDTO, UserDetails userDetails) {
        // 检查课程是否存在
        DualTeacherCourse course = courseMapper.selectById(ratingDTO.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        // 获取学生ID
        String username = userDetails.getUsername();
        Integer studentId = getStudentId(username);
        
        // 检查学生是否已报名该课程
        LambdaQueryWrapper<CourseEnrollment> enrollmentQuery = new LambdaQueryWrapper<>();
        enrollmentQuery.eq(CourseEnrollment::getCourseId, ratingDTO.getCourseId())
                       .eq(CourseEnrollment::getStudentId, studentId);
        CourseEnrollment enrollment = enrollmentMapper.selectOne(enrollmentQuery);
        
        if (enrollment == null) {
            throw new BusinessException("您未报名该课程，无法评价");
        }
        
        // 检查课程是否已完成
        if (!"completed".equals(course.getStatus())) {
            throw new BusinessException("课程尚未完成，暂不能评价");
        }
        
        // 检查是否已评价过
        if (hasRated(ratingDTO.getCourseId(), userDetails)) {
            throw new BusinessException("您已评价过该课程");
        }
        
        // 保存评价
        CourseRating rating = new CourseRating();
        rating.setCourseId(ratingDTO.getCourseId());
        rating.setStudentId(studentId);
        rating.setRating(ratingDTO.getRating());
        rating.setComment(ratingDTO.getComment());
        rating.setIsAnonymous(ratingDTO.getIsAnonymous());
        rating.setCreatedAt(LocalDateTime.now());
        rating.setUpdatedAt(LocalDateTime.now());
        rating.setIsDeleted(false);
        
        ratingMapper.insert(rating);
        
        // 转换为VO并返回
        return convertToVO(rating);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseRatingVO updateRating(Integer ratingId, CourseRatingDTO ratingDTO, UserDetails userDetails) {
        // 检查评价是否存在
        CourseRating rating = ratingMapper.selectById(ratingId);
        if (rating == null) {
            throw new BusinessException("评价不存在");
        }
        
        // 检查是否是本人的评价
        Integer studentId = getStudentId(userDetails.getUsername());
        if (!Objects.equals(rating.getStudentId(), studentId)) {
            throw new BusinessException("无权修改他人的评价");
        }
        
        // 更新评价
        rating.setRating(ratingDTO.getRating());
        rating.setComment(ratingDTO.getComment());
        rating.setIsAnonymous(ratingDTO.getIsAnonymous());
        rating.setUpdatedAt(LocalDateTime.now());
        
        ratingMapper.updateById(rating);
        
        // 转换为VO并返回
        return convertToVO(rating);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRating(Integer ratingId, UserDetails userDetails) {
        // 检查评价是否存在
        CourseRating rating = ratingMapper.selectById(ratingId);
        if (rating == null) {
            throw new BusinessException("评价不存在");
        }
        
        // 检查是否是本人的评价
        Integer studentId = getStudentId(userDetails.getUsername());
        if (!Objects.equals(rating.getStudentId(), studentId)) {
            throw new BusinessException("无权删除他人的评价");
        }
        
        // 逻辑删除评价
        ratingMapper.deleteById(ratingId);
    }

    @Override
    public CourseRatingVO getRatingById(Integer ratingId) {
        CourseRating rating = ratingMapper.selectById(ratingId);
        if (rating == null) {
            throw new BusinessException("评价不存在");
        }
        
        return convertToVO(rating);
    }

    @Override
    public IPage<CourseRatingVO> getRatingsByCourseId(Integer courseId, int page, int size) {
        // 检查课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        // 分页查询
        Page<CourseRating> pageParam = new Page<>(page, size);
        IPage<CourseRating> ratingPage = ratingMapper.selectPageByCourseId(pageParam, courseId);
        
        // 转换为VO
        return ratingPage.convert(this::convertToVO);
    }

    @Override
    public Double getAverageRating(Integer courseId) {
        // 检查课程是否存在
        DualTeacherCourse course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        return ratingMapper.selectAvgRatingByCourseId(courseId);
    }

    @Override
    public boolean hasRated(Integer courseId, UserDetails userDetails) {
        Integer studentId = getStudentId(userDetails.getUsername());
        Integer count = ratingMapper.countByCourseIdAndStudentId(courseId, studentId);
        return count != null && count > 0;
    }
    
    /**
     * 将实体转换为VO
     */
    private CourseRatingVO convertToVO(CourseRating rating) {
        CourseRatingVO vo = new CourseRatingVO();
        BeanUtils.copyProperties(rating, vo);
        
        // 如果是匿名评价，则隐藏学生信息
        if (Boolean.TRUE.equals(rating.getIsAnonymous())) {
            vo.setStudentName("匿名用户");
        } else {
            // 获取学生姓名（实际项目中应该从用户服务获取）
            vo.setStudentName("学生" + rating.getStudentId());
        }
        
        return vo;
    }
    
    /**
     * 从用户名获取学生ID（实际项目中应该从用户服务获取）
     */
    private Integer getStudentId(String username) {
        // 模拟实现，实际项目中应该从用户服务获取
        return 1;
    }
} 