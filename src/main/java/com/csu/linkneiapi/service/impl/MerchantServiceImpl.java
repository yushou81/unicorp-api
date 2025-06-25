package com.csu.linkneiapi.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.linkneiapi.common.exception.BusinessException;
import com.csu.linkneiapi.entity.Merchant;
import com.csu.linkneiapi.entity.User;
import com.csu.linkneiapi.mapper.MerchantMapper;
import com.csu.linkneiapi.mapper.ProductMapper;
import com.csu.linkneiapi.mapper.UserMapper;
import com.csu.linkneiapi.service.MerchantService;
import com.csu.linkneiapi.vo.MerchantDetailVO;
import com.csu.linkneiapi.vo.MerchantSummaryVO;
import com.csu.linkneiapi.vo.PageResultVO;
import com.csu.linkneiapi.vo.ProductSummaryVO;
import com.csu.linkneiapi.dto.MerchantRegisterDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商户Service实现类
 */
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    private final ProductMapper productMapper;
    @Autowired
    private UserMapper userMapper;

    public MerchantServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public PageResultVO<MerchantSummaryVO> getMerchantList(Integer page, Integer pageSize) {
        // 参数校验
        page = (page == null || page < 1) ? 1 : page;
        pageSize = (pageSize == null || pageSize < 1 || pageSize > 100) ? 10 : pageSize;
        
        // 创建分页对象
        Page<MerchantSummaryVO> pageParam = new Page<>(page, pageSize);
        
        // 调用Mapper查询
        IPage<MerchantSummaryVO> pageResult = baseMapper.selectMerchantSummaryPage(pageParam);
        
        // 封装返回结果
        return new PageResultVO<>(
                pageResult.getTotal(),
                pageResult.getPages(),
                pageResult.getCurrent(),
                pageResult.getSize(),
                pageResult.getRecords()
        );
    }

    @Override
    public MerchantDetailVO getMerchantDetail(Long id) {
        // 参数校验
        if (id == null) {
            throw new BusinessException("商户ID不能为空");
        }
        
        // 查询商户基本信息
        Merchant merchant = baseMapper.selectMerchantDetailById(id);
        if (merchant == null) {
            throw new BusinessException("商户不存在");
        }
        
        // 查询商户产品列表
        List<ProductSummaryVO> products = productMapper.selectProductsByMerchantId(id);
        
        // 封装返回结果
        MerchantDetailVO detailVO = new MerchantDetailVO();
        BeanUtils.copyProperties(merchant, detailVO);
        detailVO.setProducts(products);
        
        return detailVO;
    }

    @Override
    @Transactional
    public void registerMerchant(Long userId, MerchantRegisterDTO dto) {
        // 1. 查询当前用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!"USER".equals(user.getRole())) {
            throw new BusinessException("只有普通用户才能注册为商户");
        }
        // 2. 检查该用户是否已注册为商户（防止重复）
        Long count = this.lambdaQuery().eq(Merchant::getUserId, userId).count();
        if (count != null && count > 0) {
            throw new BusinessException("该用户已注册为商户");
        }
        // 3. 创建商户信息
        Merchant merchant = new Merchant();
        merchant.setUserId(userId);
        merchant.setName(dto.getName());
        merchant.setAddress(dto.getAddress());
        merchant.setPhone(dto.getPhone());
        merchant.setDescription(dto.getDescription());
        merchant.setLogoUrl(dto.getLogoUrl());
        merchant.setBusinessHours(dto.getBusinessHours());
        if (dto.getLatitude() != null) {
            merchant.setLatitude(new java.math.BigDecimal(dto.getLatitude()));
        }
        if (dto.getLongitude() != null) {
            merchant.setLongitude(new java.math.BigDecimal(dto.getLongitude()));
        }
        merchant.setStatus("PENDING_REVIEW"); // 默认待审核
        this.save(merchant);
        // 4. 更新用户角色为MERCHANT
        user.setRole("MERCHANT");
        userMapper.updateById(user);
    }
} 