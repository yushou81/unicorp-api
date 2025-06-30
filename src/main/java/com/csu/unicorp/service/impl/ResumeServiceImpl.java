package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.exception.ResourceNotFoundException;
import com.csu.unicorp.dto.ResumeCreationDTO;
import com.csu.unicorp.dto.ResumeUpdateDTO;
import com.csu.unicorp.entity.Resume;
import com.csu.unicorp.mapper.ResumeMapper;
import com.csu.unicorp.service.ResumeService;
import com.csu.unicorp.vo.ResumeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 简历服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume> implements ResumeService {
    
    private final ResumeMapper resumeMapper;
    
    @Override
    @Deprecated
    public ResumeVO getUserResume(Integer userId) {
        // 查询用户的简历（默认返回第一份）
        Resume resume = resumeMapper.selectByUserId(userId);
        if (resume == null) {
            throw new ResourceNotFoundException("简历不存在");
        }
        
        return convertToVO(resume);
    }
    
    @Override
    public List<ResumeVO> getUserResumes(Integer userId) {
        // 查询用户的所有简历
        LambdaQueryWrapper<Resume> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Resume::getUserId, userId);
        List<Resume> resumes = list(queryWrapper);
        
        // 转换为VO
        return resumes.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public ResumeVO getResumeById(Integer resumeId) {
        // 查询指定ID的简历
        Resume resume = getById(resumeId);
        if (resume == null) {
            throw new ResourceNotFoundException("简历不存在");
        }
        
        return convertToVO(resume);
    }
    
    @Override
    @Transactional
    public ResumeVO createResume(Integer userId, ResumeCreationDTO dto) {
        // 创建简历实体
        Resume resume = new Resume();
        resume.setUserId(userId);
        BeanUtils.copyProperties(dto, resume);
        
        // 保存简历
        save(resume);
        
        return convertToVO(resume);
    }
    
    @Override
    @Transactional
    public ResumeVO updateResume(Integer userId, Integer resumeId, ResumeUpdateDTO dto) {
        // 查询简历
        Resume resume = getById(resumeId);
        if (resume == null) {
            throw new ResourceNotFoundException("简历不存在");
        }
        
        // 验证简历所有权
        if (!resume.getUserId().equals(userId)) {
            throw new BusinessException("无权限修改此简历");
        }
        
        // 更新简历
        if (dto.getMajor() != null) {
            resume.setMajor(dto.getMajor());
        }
        if (dto.getEducationLevel() != null) {
            resume.setEducationLevel(dto.getEducationLevel());
        }
        if (dto.getResumeUrl() != null) {
            resume.setResumeUrl(dto.getResumeUrl());
        }
        if (dto.getAchievements() != null) {
            resume.setAchievements(dto.getAchievements());
        }
        
        // 保存更新
        updateById(resume);
        
        return convertToVO(resume);
    }
    
    @Override
    @Transactional
    public void deleteResume(Integer userId, Integer resumeId) {
        // 查询简历
        Resume resume = getById(resumeId);
        if (resume == null) {
            throw new ResourceNotFoundException("简历不存在");
        }
        
        // 验证简历所有权
        if (!resume.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除此简历");
        }
        
        // 删除简历
        removeById(resumeId);
    }
    
    /**
     * 将实体转换为VO
     */
    private ResumeVO convertToVO(Resume resume) {
        ResumeVO vo = new ResumeVO();
        BeanUtils.copyProperties(resume, vo);
        return vo;
    }
} 