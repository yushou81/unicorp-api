package com.csu.unicorp.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.dto.achievement.PortfolioItemCreationDTO;
import com.csu.unicorp.dto.achievement.PortfolioResourceUploadDTO;
import com.csu.unicorp.vo.achievement.PortfolioItemVO;
import com.csu.unicorp.vo.achievement.PortfolioResourceVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 作品集Service接口
 */
public interface PortfolioService {
    
    /**
     * 获取用户的作品列表
     * 
     * @param userId 用户ID
     * @return 作品列表
     */
    List<PortfolioItemVO> getPortfolioItems(Integer userId);
    
    /**
     * 分页获取用户的作品列表
     * 
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 作品分页列表
     */
    Page<PortfolioItemVO> getPortfolioItemPage(Integer userId, Integer page, Integer size);
    
    /**
     * 分页获取公开的作品列表
     * 
     * @param page 页码
     * @param size 每页大小
     * @return 公开作品分页列表
     */
    Page<PortfolioItemVO> getPublicPortfolioItemPage(Integer page, Integer size);
    
    /**
     * 根据分类分页获取公开的作品列表
     * 
     * @param category 作品分类
     * @param page 页码
     * @param size 每页大小
     * @return 公开作品分页列表
     */
    Page<PortfolioItemVO> getPublicPortfolioItemPageByCategory(String category, Integer page, Integer size);
    
    /**
     * 获取作品详情
     * 
     * @param id 作品ID
     * @param viewerIp 查看者IP，用于记录访问记录
     * @return 作品详情
     */
    PortfolioItemVO getPortfolioItemDetail(Integer id, String viewerIp);
    
    /**
     * 创建作品
     * 
     * @param userId 用户ID
     * @param portfolioItemCreationDTO 作品创建DTO
     * @param coverImage 封面图片文件
     * @return 创建成功的作品
     */
    PortfolioItemVO createPortfolioItem(Integer userId, PortfolioItemCreationDTO portfolioItemCreationDTO, MultipartFile coverImage);
    
    /**
     * 更新作品
     * 
     * @param id 作品ID
     * @param userId 用户ID
     * @param portfolioItemCreationDTO 作品创建DTO
     * @param coverImage 封面图片文件
     * @return 更新后的作品
     */
    PortfolioItemVO updatePortfolioItem(Integer id, Integer userId, PortfolioItemCreationDTO portfolioItemCreationDTO, MultipartFile coverImage);
    
    /**
     * 删除作品
     * 
     * @param id 作品ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deletePortfolioItem(Integer id, Integer userId);
    
    /**
     * 上传作品资源
     * 
     * @param portfolioItemId 作品ID
     * @param userId 用户ID
     * @param resourceUploadDTO 资源上传DTO
     * @return 上传成功的资源
     */
    PortfolioResourceVO uploadResource(Integer portfolioItemId, Integer userId, PortfolioResourceUploadDTO resourceUploadDTO);
    
    /**
     * 上传作品资源文件
     * 
     * @param portfolioItemId 作品ID
     * @param userId 用户ID
     * @param file 文件
     * @param resourceType 资源类型
     * @param description 资源描述
     * @return 上传成功的资源
     */
    PortfolioResourceVO uploadResourceFile(Integer portfolioItemId, Integer userId, MultipartFile file, String resourceType, String description);
    
    /**
     * 删除作品资源
     * 
     * @param portfolioItemId 作品ID
     * @param resourceId 资源ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteResource(Integer portfolioItemId, Integer resourceId, Integer userId);
    
    /**
     * 点赞作品
     * 
     * @param id 作品ID
     * @return 是否点赞成功
     */
    boolean likePortfolioItem(Integer id);
}