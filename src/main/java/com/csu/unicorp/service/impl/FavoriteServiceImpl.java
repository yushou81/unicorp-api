package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.exception.ResourceNotFoundException;
import com.csu.unicorp.entity.job.Job;
import com.csu.unicorp.entity.job.JobFavorite;
import com.csu.unicorp.mapper.JobFavoriteMapper;
import com.csu.unicorp.mapper.JobMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.FavoriteService;
import com.csu.unicorp.vo.JobVO;
import com.csu.unicorp.vo.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 岗位收藏服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final JobFavoriteMapper jobFavoriteMapper;
    private final JobMapper jobMapper;
    private final UserMapper userMapper;
    
    @Override
    @Transactional
    public ResultVO<Void> favoriteJob(Integer userId, Integer jobId) {
        // 验证用户是否为学生
        validateStudentRole(userId);
        
        // 验证岗位是否存在
        Job job = jobMapper.selectById(jobId);
        if (job == null || job.getIsDeleted()) {
            throw new ResourceNotFoundException("岗位不存在");
        }
        
        // 检查是否已收藏
        if (checkFavorite(userId, jobId)) {
            return ResultVO.error("已收藏过该岗位");
        }
        
        // 创建收藏记录
        JobFavorite jobFavorite = new JobFavorite();
        jobFavorite.setUserId(userId);
        jobFavorite.setJobId(jobId);
        jobFavorite.setCreatedAt(LocalDateTime.now());
        
        // 保存收藏记录
        jobFavoriteMapper.insert(jobFavorite);
        
        return ResultVO.success("收藏成功");
    }
    
    @Override
    @Transactional
    public ResultVO<Void> unfavoriteJob(Integer userId, Integer jobId) {
        // 验证用户是否为学生
        validateStudentRole(userId);
        
        // 删除收藏记录
        LambdaQueryWrapper<JobFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JobFavorite::getUserId, userId)
                .eq(JobFavorite::getJobId, jobId);
        
        jobFavoriteMapper.delete(queryWrapper);
        
        return ResultVO.success("取消收藏成功");
    }
    
    @Override
    public IPage<JobVO> getFavoriteJobs(Integer userId, int page, int size) {
        // 验证用户是否为学生
        validateStudentRole(userId);
        
        // 查询收藏的岗位
        Page<JobVO> pageParam = new Page<>(page, size);
        IPage<JobVO> jobVOPage = jobFavoriteMapper.selectFavoriteJobsByUserId(pageParam, userId);
        
        return jobVOPage;
    }
    
    @Override
    public boolean checkFavorite(Integer userId, Integer jobId) {
        Integer count = jobFavoriteMapper.checkFavorite(userId, jobId);
        return count != null && count > 0;
    }
    
    /**
     * 验证用户是否为学生角色
     * 
     * @param userId 用户ID
     */
    private void validateStudentRole(Integer userId) {
        // 获取用户角色
        String role = userMapper.selectRoleByUserId(userId);
        
        // 验证是否为学生角色
        if (!RoleConstants.DB_ROLE_STUDENT.equals(role)) {
            throw new BusinessException("只有学生用户才能收藏岗位");
        }
    }
} 