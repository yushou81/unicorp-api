package com.csu.linkneiapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.linkneiapi.entity.Merchant;
import com.csu.linkneiapi.vo.MerchantSummaryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商户Mapper接口
 */
@Mapper
public interface MerchantMapper extends BaseMapper<Merchant> {
    
    /**
     * 分页查询商户列表
     * @param page 分页参数
     * @return 商户列表分页结果
     */
    IPage<MerchantSummaryVO> selectMerchantSummaryPage(Page<MerchantSummaryVO> page);
    
    /**
     * 根据ID查询商户详情，包括产品列表
     * @param id 商户ID
     * @return 商户详情
     */
    Merchant selectMerchantDetailById(@Param("id") Long id);
} 