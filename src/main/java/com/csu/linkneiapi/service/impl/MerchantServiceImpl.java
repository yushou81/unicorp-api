package com.csu.linkneiapi.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.linkneiapi.common.exception.BusinessException;
import com.csu.linkneiapi.entity.Merchant;
import com.csu.linkneiapi.mapper.MerchantMapper;
import com.csu.linkneiapi.mapper.ProductMapper;
import com.csu.linkneiapi.service.MerchantService;
import com.csu.linkneiapi.vo.MerchantDetailVO;
import com.csu.linkneiapi.vo.MerchantSummaryVO;
import com.csu.linkneiapi.vo.PageResultVO;
import com.csu.linkneiapi.vo.ProductSummaryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商户Service实现类
 */
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    private final ProductMapper productMapper;

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
} 