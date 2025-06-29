package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csu.unicorp.common.constants.RoleConstants;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.exception.ResourceNotFoundException;
import com.csu.unicorp.dto.PortfolioItemCreationDTO;
import com.csu.unicorp.entity.PortfolioItem;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.PortfolioItemMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.PortfolioService;
import com.csu.unicorp.vo.PortfolioItemVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 作品集服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioItemMapper portfolioItemMapper;
    private final UserMapper userMapper;
    
    @Override
    public List<PortfolioItemVO> getPortfolioItems(Integer userId) {
        // 查询用户的所有作品集项目
        List<PortfolioItem> portfolioItems = portfolioItemMapper.selectByUserId(userId);
        
        // 转换为VO
        return portfolioItems.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public PortfolioItemVO addPortfolioItem(Integer userId, PortfolioItemCreationDTO portfolioItemCreationDTO) {
        // 验证用户是否为学生
        validateStudentRole(userId);
        
        // 创建作品集项目
        PortfolioItem portfolioItem = new PortfolioItem();
        portfolioItem.setUserId(userId);
        portfolioItem.setTitle(portfolioItemCreationDTO.getTitle());
        portfolioItem.setDescription(portfolioItemCreationDTO.getDescription());
        portfolioItem.setProjectUrl(portfolioItemCreationDTO.getProjectUrl());
        portfolioItem.setCoverImageUrl(portfolioItemCreationDTO.getCoverImageUrl());
        portfolioItem.setCreatedAt(LocalDateTime.now());
        portfolioItem.setUpdatedAt(LocalDateTime.now());
        portfolioItem.setIsDeleted(false);
        
        // 保存到数据库
        portfolioItemMapper.insert(portfolioItem);
        
        // 返回VO
        return convertToVO(portfolioItem);
    }
    
    @Override
    @Transactional
    public PortfolioItemVO updatePortfolioItem(Integer userId, Integer itemId, PortfolioItemCreationDTO portfolioItemCreationDTO) {
        // 验证用户是否为学生
        validateStudentRole(userId);
        
        // 查询作品集项目
        PortfolioItem portfolioItem = getPortfolioItemAndValidateOwnership(userId, itemId);
        
        // 更新作品集项目
        portfolioItem.setTitle(portfolioItemCreationDTO.getTitle());
        portfolioItem.setDescription(portfolioItemCreationDTO.getDescription());
        portfolioItem.setProjectUrl(portfolioItemCreationDTO.getProjectUrl());
        portfolioItem.setCoverImageUrl(portfolioItemCreationDTO.getCoverImageUrl());
        portfolioItem.setUpdatedAt(LocalDateTime.now());
        
        // 保存到数据库
        portfolioItemMapper.updateById(portfolioItem);
        
        // 返回VO
        return convertToVO(portfolioItem);
    }
    
    @Override
    @Transactional
    public void deletePortfolioItem(Integer userId, Integer itemId) {
        // 验证用户是否为学生
        validateStudentRole(userId);
        
        // 查询作品集项目
        PortfolioItem portfolioItem = getPortfolioItemAndValidateOwnership(userId, itemId);
        
        // 逻辑删除
        portfolioItemMapper.deleteById(itemId);
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
            throw new BusinessException("只有学生用户才能管理作品集");
        }
    }
    
    /**
     * 获取作品集项目并验证所有权
     * 
     * @param userId 用户ID
     * @param itemId 项目ID
     * @return 作品集项目
     */
    private PortfolioItem getPortfolioItemAndValidateOwnership(Integer userId, Integer itemId) {
        // 查询作品集项目
        PortfolioItem portfolioItem = portfolioItemMapper.selectById(itemId);
        if (portfolioItem == null) {
            throw new ResourceNotFoundException("作品集项目不存在");
        }
        
        // 验证所有权
        if (!portfolioItem.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此作品集项目");
        }
        
        return portfolioItem;
    }
    
    /**
     * 将实体转换为VO
     * 
     * @param portfolioItem 作品集项目实体
     * @return 作品集项目VO
     */
    private PortfolioItemVO convertToVO(PortfolioItem portfolioItem) {
        PortfolioItemVO vo = new PortfolioItemVO();
        BeanUtils.copyProperties(portfolioItem, vo);
        return vo;
    }
} 