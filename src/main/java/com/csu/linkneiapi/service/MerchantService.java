package com.csu.linkneiapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.csu.linkneiapi.entity.Merchant;
import com.csu.linkneiapi.vo.MerchantDetailVO;
import com.csu.linkneiapi.vo.PageResultVO;
import com.csu.linkneiapi.vo.MerchantSummaryVO;
import com.csu.linkneiapi.dto.MerchantRegisterDTO;

/**
 * 商户Service接口
 */
public interface MerchantService extends IService<Merchant> {
    
    /**
     * 分页查询商户列表
     * @param page 页码
     * @param pageSize 每页大小
     * @return 商户列表分页结果
     */
    PageResultVO<MerchantSummaryVO> getMerchantList(Integer page, Integer pageSize);
    
    /**
     * 根据ID查询商户详情
     * @param id 商户ID
     * @return 商户详情
     */
    MerchantDetailVO getMerchantDetail(Long id);

    /**
     * 商户注册
     * @param userId 当前用户ID
     * @param dto 商户注册信息
     */
    void registerMerchant(Long userId, MerchantRegisterDTO dto);
} 