package com.csu.unicorp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.dto.achievement.AchievementVerifyDTO;
import com.csu.unicorp.dto.achievement.CompetitionAwardCreationDTO;
import com.csu.unicorp.vo.achievement.CompetitionAwardVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 竞赛获奖Service接口
 */
public interface CompetitionAwardService {
    
    /**
     * 获取用户的获奖列表
     * 
     * @param userId 用户ID
     * @return 获奖列表
     */
    List<CompetitionAwardVO> getCompetitionAwards(Integer userId);
    
    /**
     * 分页获取用户的获奖列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 获奖分页列表
     */
    Page<CompetitionAwardVO> getCompetitionAwardPage(Integer userId, Integer page, Integer size);
    
    /**
     * 分页获取公开的获奖列表
     * 
     * @param page 页码
     * @param size 每页大小
     * @return 公开获奖分页列表
     */
    Page<CompetitionAwardVO> getPublicCompetitionAwardPage(Integer page, Integer size);
    
    /**
     * 分页获取待认证的获奖列表
     * 
     * @param organizationId 组织ID
     * @param currentUserId 当前用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 待认证获奖分页列表
     */
    Page<CompetitionAwardVO> getUnverifiedCompetitionAwardPage(Integer organizationId, Integer currentUserId, Integer page, Integer size);
    
    /**
     * 获取获奖详情
     * 
     * @param id 获奖ID
     * @param viewerIp 查看者IP，用于记录访问记录
     * @return 获奖详情
     */
    CompetitionAwardVO getCompetitionAwardDetail(Integer id, String viewerIp);
    
    /**
     * 创建获奖
     * 
     * @param userId 用户ID
     * @param competitionAwardCreationDTO 获奖创建DTO
     * @return 创建成功的获奖
     */
    CompetitionAwardVO createCompetitionAward(Integer userId, CompetitionAwardCreationDTO competitionAwardCreationDTO);
    
    /**
     * 更新获奖
     * 
     * @param id 获奖ID
     * @param userId 用户ID
     * @param competitionAwardCreationDTO 获奖创建DTO
     * @return 更新后的获奖
     */
    CompetitionAwardVO updateCompetitionAward(Integer id, Integer userId, CompetitionAwardCreationDTO competitionAwardCreationDTO);
    
    /**
     * 删除获奖
     * 
     * @param id 获奖ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteCompetitionAward(Integer id, Integer userId);
    
    /**
     * 上传获奖证书
     * 
     * @param id 获奖ID
     * @param userId 用户ID
     * @param file 证书文件
     * @return 上传后的获奖
     */
    CompetitionAwardVO uploadCertificate(Integer id, Integer userId, MultipartFile file);
    
    /**
     * 认证获奖
     * 
     * @param id 获奖ID
     * @param verifierId 认证人ID
     * @param verifyDTO 认证DTO
     * @return 认证后的获奖
     */
    CompetitionAwardVO verifyCompetitionAward(Integer id, Integer verifierId, AchievementVerifyDTO verifyDTO);
    
    /**
     * 获取学校学生竞赛获奖列表
     * 
     * @param userId 当前教师或管理员ID
     * @param page 页码
     * @param size 每页大小
     * @return 学生竞赛获奖分页列表
     */
    Page<CompetitionAwardVO> getSchoolStudentAwards(Integer userId, int page, int size);
    
    /**
     * 获取学校竞赛获奖统计数据
     * 
     * @param userId 当前教师或管理员ID
     * @return 学校竞赛获奖统计数据
     */
    Map<String, Object> getSchoolAwardStatistics(Integer userId);
    
    /**
     * 获取学校指定学生的竞赛获奖列表
     * 
     * @param userId 当前教师或管理员ID
     * @param studentId 学生ID
     * @return 学生竞赛获奖列表
     */
    List<CompetitionAwardVO> getSchoolStudentAwardsByStudent(Integer userId, Integer studentId);
} 