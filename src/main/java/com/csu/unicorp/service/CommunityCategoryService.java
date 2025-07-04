package com.csu.unicorp.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.csu.unicorp.dto.community.CategoryDTO;
import com.csu.unicorp.entity.community.CommunityCategory;
import com.csu.unicorp.vo.community.CategoryVO;
import com.csu.unicorp.vo.community.DeleteResult;

/**
 * 社区板块Service接口
 */
public interface CommunityCategoryService extends IService<CommunityCategory> {
    
    /**
     * 获取板块树形结构
     * @return 板块树形结构
     */
    List<CategoryVO> getCategoryTree();
    
    /**
     * 获取板块详情
     * @param categoryId 板块ID
     * @return 板块详情
     */
    CategoryVO getCategoryDetail(Long categoryId);
    
    /**
     * 创建板块
     * @param categoryDTO 板块DTO
     * @return 板块ID
     */
    Long createCategory(CategoryDTO categoryDTO);
    
    /**
     * 更新板块
     * @param categoryId 板块ID
     * @param categoryDTO 板块DTO
     * @return 是否成功
     */
    boolean updateCategory(Long categoryId, CategoryDTO categoryDTO);
    
    /**
     * 删除板块
     * @param categoryId 板块ID
     * @return 删除结果，包含成功状态和错误信息
     */
    DeleteResult deleteCategory(Long categoryId);
    
    /**
     * 获取板块列表（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 板块列表
     */
    Page<CategoryVO> listCategories(int page, int size);
    
    /**
     * 更新板块排序
     * @param categoryId 板块ID
     * @param sortOrder 排序顺序
     * @return 是否成功
     */
    boolean updateCategorySortOrder(Long categoryId, Integer sortOrder);
    
    /**
     * 获取用户可见的板块列表
     * @param userId 用户ID
     * @return 板块列表
     */
    List<CategoryVO> getUserVisibleCategories(Long userId);
    
    /**
     * 检查用户是否有权限访问板块
     * @param userId 用户ID
     * @param categoryId 板块ID
     * @return 是否有权限
     */
    boolean checkUserCategoryPermission(Long userId, Long categoryId);
} 