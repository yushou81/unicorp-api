package com.csu.unicorp.service.impl.community;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.constants.CommunityConstants;
import com.csu.unicorp.dto.community.CategoryDTO;
import com.csu.unicorp.entity.community.CommunityCategory;
import com.csu.unicorp.mapper.community.CommunityCategoryMapper;
import com.csu.unicorp.service.CommunityCategoryService;
import com.csu.unicorp.vo.community.CategoryVO;
import com.csu.unicorp.vo.community.DeleteResult;

import lombok.RequiredArgsConstructor;

/**
 * 社区板块Service实现类
 */
@Service
@RequiredArgsConstructor
public class CommunityCategoryServiceImpl extends ServiceImpl<CommunityCategoryMapper, CommunityCategory> implements CommunityCategoryService {
    
    private final CommunityCategoryMapper categoryMapper;
    
    /**
     * 获取板块树形结构
     * @return 板块树形结构
     */
    @Override
    public List<CategoryVO> getCategoryTree() {
        // 获取所有板块
        List<CommunityCategory> allCategories = this.list();
        
        // 转换为VO
        List<CategoryVO> categoryVOList = allCategories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 构建树形结构
        return buildCategoryTree(categoryVOList);
    }
    
    /**
     * 获取板块详情
     * @param categoryId 板块ID
     * @return 板块详情
     */
    @Override
    public CategoryVO getCategoryDetail(Long categoryId) {
        CommunityCategory category = this.getById(categoryId);
        if (category == null) {
            return null;
        }
        
        CategoryVO categoryVO = convertToVO(category);
        
        // 获取子板块
        LambdaQueryWrapper<CommunityCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityCategory::getParentId, categoryId);
        List<CommunityCategory> children = this.list(queryWrapper);
        
        if (!children.isEmpty()) {
            categoryVO.setChildren(children.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList()));
        }
        
        // 获取话题数量
        Integer topicCount = categoryMapper.countTopicsByCategoryId(categoryId);
        categoryVO.setTopicCount(topicCount);
        
        return categoryVO;
    }
    
    /**
     * 创建板块
     * @param categoryDTO 板块DTO
     * @return 板块ID
     */
    @Override
    @Transactional
    public Long createCategory(CategoryDTO categoryDTO) {
        CommunityCategory category = new CommunityCategory();
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setIcon(categoryDTO.getIcon());
        category.setSortOrder(categoryDTO.getSortOrder() != null ? categoryDTO.getSortOrder() : 0);
        category.setParentId(categoryDTO.getParentId());
        category.setPermissionLevel(categoryDTO.getPermissionLevel() != null ? 
                categoryDTO.getPermissionLevel() : CommunityConstants.PermissionLevel.PUBLIC);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        this.save(category);
        return category.getId();
    }
    
    /**
     * 更新板块
     * @param categoryId 板块ID
     * @param categoryDTO 板块DTO
     * @return 是否成功
     */
    @Override
    @Transactional
    public boolean updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        CommunityCategory category = this.getById(categoryId);
        if (category == null) {
            return false;
        }
        
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setIcon(categoryDTO.getIcon());
        if (categoryDTO.getSortOrder() != null) {
            category.setSortOrder(categoryDTO.getSortOrder());
        }
        category.setParentId(categoryDTO.getParentId());
        if (categoryDTO.getPermissionLevel() != null) {
            category.setPermissionLevel(categoryDTO.getPermissionLevel());
        }
        category.setUpdatedAt(LocalDateTime.now());
        
        return this.updateById(category);
    }
    
    /**
     * 删除板块
     * @param categoryId 板块ID
     * @return 删除结果，包含成功状态和错误信息
     */
    @Override
    @Transactional
    public DeleteResult deleteCategory(Long categoryId) {
        CommunityCategory category = this.getById(categoryId);
        if (category == null) {
            return new DeleteResult(false, "板块不存在");
        }
        
        // 检查是否有子板块
        LambdaQueryWrapper<CommunityCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CommunityCategory::getParentId, categoryId);
        long childCount = this.count(queryWrapper);
        
        if (childCount > 0) {
            // 有子板块，不能删除
            return new DeleteResult(false, "该板块下存在子板块，无法删除");
        }
        
        // 检查是否有话题
        Integer topicCount = categoryMapper.countTopicsByCategoryId(categoryId);
        if (topicCount > 0) {
            // 有话题，不能删除
            return new DeleteResult(false, "该板块下存在话题，无法删除");
        }
        
        boolean success = this.removeById(categoryId);
        return new DeleteResult(success, success ? null : "删除失败，请稍后重试");
    }
    
    /**
     * 获取板块列表（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 板块列表
     */
    @Override
    public Page<CategoryVO> listCategories(int page, int size) {
        Page<CommunityCategory> categoryPage = new Page<>(page, size);
        Page<CommunityCategory> resultPage = categoryMapper.selectCategoriesWithTopicCount(categoryPage);
        
        // 转换为VO
        Page<CategoryVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<CategoryVO> records = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(records);
        
        return voPage;
    }
    
    /**
     * 更新板块排序
     * @param categoryId 板块ID
     * @param sortOrder 排序顺序
     * @return 是否成功
     */
    @Override
    public boolean updateCategorySortOrder(Long categoryId, Integer sortOrder) {
        CommunityCategory category = this.getById(categoryId);
        if (category == null) {
            return false;
        }
        
        category.setSortOrder(sortOrder);
        category.setUpdatedAt(LocalDateTime.now());
        
        return this.updateById(category);
    }
    
    /**
     * 获取用户可见的板块列表
     * @param userId 用户ID
     * @return 板块列表
     */
    @Override
    public List<CategoryVO> getUserVisibleCategories(Long userId) {
        // 获取所有板块
        List<CommunityCategory> allCategories = this.list();
        
        // 根据权限过滤
        List<CommunityCategory> visibleCategories;
        if (userId == null) {
            // 未登录用户只能看到公开的板块
            visibleCategories = allCategories.stream()
                    .filter(c -> c.getPermissionLevel() == CommunityConstants.PermissionLevel.PUBLIC)
                    .collect(Collectors.toList());
        } else {
            // 登录用户可以看到公开和登录可见的板块
            // 注意：这里应该根据用户角色和组织关系进行更复杂的权限判断
            // 简化处理，假设普通用户只能看到公开和登录可见的板块
            visibleCategories = allCategories.stream()
                    .filter(c -> c.getPermissionLevel() <= CommunityConstants.PermissionLevel.LOGIN)
                    .collect(Collectors.toList());
        }
        
        // 转换为VO
        List<CategoryVO> categoryVOList = visibleCategories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 构建树形结构
        return buildCategoryTree(categoryVOList);
    }
    
    /**
     * 检查用户是否有权限访问板块
     * @param userId 用户ID
     * @param categoryId 板块ID
     * @return 是否有权限
     */
    @Override
    public boolean checkUserCategoryPermission(Long userId, Long categoryId) {
        CommunityCategory category = this.getById(categoryId);
        if (category == null) {
            return false;
        }
        
        // 公开板块，所有人可见
        if (category.getPermissionLevel() == CommunityConstants.PermissionLevel.PUBLIC) {
            return true;
        }
        
        // 未登录用户只能访问公开板块
        if (userId == null) {
            return false;
        }
        
        // 登录可见板块，登录用户可见
        if (category.getPermissionLevel() == CommunityConstants.PermissionLevel.LOGIN) {
            return true;
        }
        
        // 组织成员可见板块，需要检查用户是否为组织成员
        // 管理员可见板块，需要检查用户是否为管理员
        // 这里需要根据实际业务逻辑进行判断
        // 简化处理，假设只有管理员可以访问权限级别为2和3的板块
        // 实际应用中应该根据用户角色和组织关系进行判断
        return false;
    }
    
    /**
     * 将实体转换为VO
     * @param category 板块实体
     * @return 板块VO
     */
    private CategoryVO convertToVO(CommunityCategory category) {
        if (category == null) {
            return null;
        }
        
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setDescription(category.getDescription());
        vo.setIcon(category.getIcon());
        vo.setSortOrder(category.getSortOrder());
        vo.setParentId(category.getParentId());
        vo.setPermissionLevel(category.getPermissionLevel());
        vo.setCreatedAt(category.getCreatedAt());
        vo.setUpdatedAt(category.getUpdatedAt());
        
        // 如果有父板块，获取父板块名称
        if (category.getParentId() != null) {
            CommunityCategory parent = this.getById(category.getParentId());
            if (parent != null) {
                vo.setParentName(parent.getName());
            }
        }
        
        return vo;
    }
    
    /**
     * 构建板块树形结构
     * @param categoryList 板块VO列表
     * @return 树形结构的板块列表
     */
    private List<CategoryVO> buildCategoryTree(List<CategoryVO> categoryList) {
        List<CategoryVO> resultList = new ArrayList<>();
        Map<Long, CategoryVO> categoryMap = new HashMap<>();
        
        // 将所有板块放入Map
        for (CategoryVO category : categoryList) {
            categoryMap.put(category.getId(), category);
        }
        
        // 构建树形结构
        for (CategoryVO category : categoryList) {
            if (category.getParentId() == null) {
                // 顶级板块
                resultList.add(category);
            } else {
                // 子板块
                CategoryVO parent = categoryMap.get(category.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(category);
                }
            }
        }
        
        return resultList;
    }
} 