package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.entity.EnterpriseDetail;
import com.csu.unicorp.entity.Organization;
import com.csu.unicorp.entity.User;
import com.csu.unicorp.mapper.EnterpriseDetailMapper;
import com.csu.unicorp.mapper.OrganizationMapper;
import com.csu.unicorp.mapper.UserMapper;
import com.csu.unicorp.service.EnterpriseService;
import com.csu.unicorp.service.OrganizationService;
import com.csu.unicorp.vo.OrganizationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 企业服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EnterpriseServiceImpl implements EnterpriseService {
    
    private final OrganizationMapper organizationMapper;
    private final EnterpriseDetailMapper enterpriseDetailMapper;
    private final UserMapper userMapper;
    private final OrganizationService organizationService;
    
    @Override
    @Transactional
    public Integer createEnterprise(Organization organization, EnterpriseDetail enterpriseDetail) {
        // 创建企业组织
        organizationMapper.insert(organization);
        
        // 设置企业详情的组织ID
        enterpriseDetail.setOrganizationId(organization.getId());
        
        // 创建企业详情
        enterpriseDetailMapper.insert(enterpriseDetail);
        
        return organization.getId();
    }
    
    @Override
    public EnterpriseDetail getEnterpriseDetailById(Integer organizationId) {
        return enterpriseDetailMapper.selectById(organizationId);
    }

    /**
     * 批准企业注册
     */
    @Override
    @Transactional
    public OrganizationVO approveEnterprise(Integer organizationId) {
        // 1. 获取企业组织信息
        Organization organization = organizationMapper.selectById(organizationId);
        if (organization == null) {
            throw new BusinessException("企业不存在");
        }
        
        if (!"pending".equals(organization.getStatus())) {
            throw new BusinessException("该企业不是待审核状态");
        }
        
        // 2. 更新企业状态为approved
        organization.setStatus("approved");
        organizationMapper.updateById(organization);
        
        // 3. 更新企业管理员状态为active
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOrganizationId, organizationId)
                    .eq(User::getStatus, "pending_approval");
        
        List<User> pendingUsers = userMapper.selectList(queryWrapper);
        for (User user : pendingUsers) {
            user.setStatus("active");
            userMapper.updateById(user);
        }
        
        // 4. 返回更新后的企业信息
        return organizationService.convertToVO(organization);
    }
} 