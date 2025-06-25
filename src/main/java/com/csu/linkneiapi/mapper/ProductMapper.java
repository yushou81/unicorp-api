package com.csu.linkneiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.linkneiapi.entity.Product;
import com.csu.linkneiapi.vo.ProductSummaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 产品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    /**
     * 根据商户ID查询产品列表
     * @param merchantId 商户ID
     * @return 产品列表
     */
    List<ProductSummaryVO> selectProductsByMerchantId(@Param("merchantId") Long merchantId);
} 