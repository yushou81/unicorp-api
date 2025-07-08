package com.csu.unicorp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.dto.achievement.AchievementVerifyDTO;
import com.csu.unicorp.dto.achievement.ResearchAchievementCreationDTO;
import com.csu.unicorp.vo.achievement.ResearchAchievementVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 科研成果Service接口
 */
public interface ResearchAchievementService {
    
    /**
     * 获取用户的科研成果列表
     * 
     * @param userId 用户ID
     * @return 科研成果列表
     */
    List<ResearchAchievementVO> getResearchAchievements(Integer userId);
    
    /**
     * 分页获取用户的科研成果列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 科研成果分页列表
     */
    Page<ResearchAchievementVO> getResearchAchievementPage(Integer userId, Integer page, Integer size);
    
    /**
     * 分页获取公开的科研成果列表
     * 
     * @param page 页码
     * @param size 每页大小
     * @return 公开科研成果分页列表
     */
    Page<ResearchAchievementVO> getPublicResearchAchievementPage(Integer page, Integer size);
    
    /**
     * 根据类型分页获取公开的科研成果列表
     * 
     * @param type 科研成果类型
     * @param page 页码
     * @param size 每页大小
     * @return 公开科研成果分页列表
     */
    Page<ResearchAchievementVO> getPublicResearchAchievementPageByType(String type, Integer page, Integer size);
    
    /**
     * 分页获取待认证的科研成果列表
     * 
     * @param organizationId 组织ID
     * @param page 页码
     * @param size 每页大小
     * @return 待认证科研成果分页列表
     */
    Page<ResearchAchievementVO> getUnverifiedResearchAchievementPage(Integer organizationId, Integer page, Integer size);
    
    /**
     * 获取科研成果详情
     * 
     * @param id 科研成果ID
     * @param viewerIp 查看者IP，用于记录访问记录
     * @return 科研成果详情
     */
    ResearchAchievementVO getResearchAchievementDetail(Integer id, String viewerIp);
    
    /**
     * 创建科研成果
     * 
     * @param userId 用户ID
     * @param researchAchievementCreationDTO 科研成果创建DTO
     * @param file 成果文件
     * @param coverImage 封面图片
     * @return 创建成功的科研成果
     */
    ResearchAchievementVO createResearchAchievement(Integer userId, ResearchAchievementCreationDTO researchAchievementCreationDTO, MultipartFile file, MultipartFile coverImage);
    
    /**
     * 更新科研成果
     * 
     * @param id 科研成果ID
     * @param userId 用户ID
     * @param researchAchievementCreationDTO 科研成果创建DTO
     * @param file 成果文件
     * @param coverImage 封面图片
     * @return 更新后的科研成果
     */
    ResearchAchievementVO updateResearchAchievement(Integer id, Integer userId, ResearchAchievementCreationDTO researchAchievementCreationDTO, MultipartFile file, MultipartFile coverImage);
    
    /**
     * 删除科研成果
     * 
     * @param id 科研成果ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteResearchAchievement(Integer id, Integer userId);
    
    /**
     * 认证科研成果
     * 
     * @param id 科研成果ID
     * @param verifierId 认证人ID
     * @param verifyDTO 认证DTO
     * @return 认证后的科研成果
     */
    ResearchAchievementVO verifyResearchAchievement(Integer id, Integer verifierId, AchievementVerifyDTO verifyDTO);
    
    /**
     * 获取学校学生科研成果列表
     * 
     * @param userId 当前教师或管理员ID
     * @param page 页码
     * @param size 每页大小
     * @return 学生科研成果分页列表
     */
    Page<ResearchAchievementVO> getSchoolStudentResearchAchievements(Integer userId, int page, int size);
    
    /**
     * 根据类型获取学校学生科研成果列表
     * 
     * @param userId 当前教师或管理员ID
     * @param type 科研成果类型
     * @param page 页码
     * @param size 每页大小
     * @return 学生科研成果分页列表
     */
    Page<ResearchAchievementVO> getSchoolStudentResearchAchievementsByType(Integer userId, String type, int page, int size);
    
    /**
     * 获取学校指定学生的科研成果列表
     * 
     * @param userId 当前教师或管理员ID
     * @param studentId 学生ID
     * @return 学生科研成果列表
     */
    List<ResearchAchievementVO> getSchoolStudentResearchAchievementsByStudent(Integer userId, Integer studentId);
    
    /**
     * 获取学校科研成果统计数据
     * 
     * @param userId 当前教师或管理员ID
     * @return 学校科研成果统计数据
     */
    Map<String, Object> getSchoolResearchStatistics(Integer userId);
    
    /**
     * 更新科研成果封面图片
     * 
     * @param id 科研成果ID
     * @param userId 用户ID
     * @param coverImage 封面图片
     * @return 更新后的科研成果
     */
    ResearchAchievementVO updateResearchAchievementCover(Integer id, Integer userId, MultipartFile coverImage);
} 