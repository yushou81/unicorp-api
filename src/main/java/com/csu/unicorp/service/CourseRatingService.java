package com.csu.unicorp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.csu.unicorp.dto.CourseRatingDTO;
import com.csu.unicorp.vo.CourseRatingVO;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 课程评价服务接口
 */
public interface CourseRatingService {
    
    /**
     * 学生提交课程评价
     * @param ratingDTO 评价信息
     * @param userDetails 当前用户
     * @return 评价视图对象
     */
    CourseRatingVO submitRating(CourseRatingDTO ratingDTO, UserDetails userDetails);
    
    /**
     * 学生更新课程评价
     * @param ratingId 评价ID
     * @param ratingDTO 评价信息
     * @param userDetails 当前用户
     * @return 更新后的评价视图对象
     */
    CourseRatingVO updateRating(Integer ratingId, CourseRatingDTO ratingDTO, UserDetails userDetails);
    
    /**
     * 学生删除课程评价
     * @param ratingId 评价ID
     * @param userDetails 当前用户
     */
    void deleteRating(Integer ratingId, UserDetails userDetails);
    
    /**
     * 获取课程评价详情
     * @param ratingId 评价ID
     * @return 评价视图对象
     */
    CourseRatingVO getRatingById(Integer ratingId);
    
    /**
     * 分页获取课程评价列表
     * @param courseId 课程ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<CourseRatingVO> getRatingsByCourseId(Integer courseId, int page, int size);
    
    /**
     * 获取课程平均评分
     * @param courseId 课程ID
     * @return 平均评分
     */
    Double getAverageRating(Integer courseId);
    
    /**
     * 检查学生是否已评价课程
     * @param courseId 课程ID
     * @param userDetails 当前用户
     * @return 是否已评价
     */
    boolean hasRated(Integer courseId, UserDetails userDetails);
} 