package com.csu.unicorp.service;

import com.csu.unicorp.dto.PortfolioItemCreationDTO;
import com.csu.unicorp.vo.PortfolioItemVO;

import java.util.List;

/**
 * 作品集服务接口
 */
public interface PortfolioService {
    
    /**
     * 获取用户的作品集列表
     * 
     * @param userId 用户ID
     * @return 作品集列表
     */
    List<PortfolioItemVO> getPortfolioItems(Integer userId);
    
    /**
     * 添加新的作品集项目
     * 
     * @param userId 用户ID
     * @param portfolioItemCreationDTO 作品集项目创建信息
     * @return 创建后的作品集项目
     */
    PortfolioItemVO addPortfolioItem(Integer userId, PortfolioItemCreationDTO portfolioItemCreationDTO);
    
    /**
     * 更新作品集项目
     * 
     * @param userId 用户ID
     * @param itemId 项目ID
     * @param portfolioItemCreationDTO 作品集项目更新信息
     * @return 更新后的作品集项目
     */
    PortfolioItemVO updatePortfolioItem(Integer userId, Integer itemId, PortfolioItemCreationDTO portfolioItemCreationDTO);
    
    /**
     * 删除作品集项目
     * 
     * @param userId 用户ID
     * @param itemId 项目ID
     */
    void deletePortfolioItem(Integer userId, Integer itemId);
} 