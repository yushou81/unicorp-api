package com.csu.unicorp.service.impl.community;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.constants.CacheConstants;
import com.csu.unicorp.common.constants.CommunityConstants;
import com.csu.unicorp.dto.community.CategoryDTO;
import com.csu.unicorp.entity.community.CommunityCategory;
import com.csu.unicorp.mapper.community.CommunityCategoryMapper;
import com.csu.unicorp.service.CacheService;
import com.csu.unicorp.service.CommunityCategoryService;
import com.csu.unicorp.vo.community.CategoryVO;
import com.csu.unicorp.vo.community.DeleteResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 社区板块Service实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityCategoryServiceImpl extends ServiceImpl<CommunityCategoryMapper, CommunityCategory> implements CommunityCategoryService {
    
    private final CommunityCategoryMapper categoryMapper;
    private final CacheService cacheService;
    
    /**
     * 获取板块树形结构
     * @return 板块树形结构
     */
    @Override
    public List<CategoryVO> getCategoryTree() {
        log.info("获取板块树形结构");
        // 尝试从缓存获取
        String cacheKey = CacheConstants.CATEGORY_TREE_CACHE_KEY;
        List<CategoryVO> cachedTree = cacheService.getList(cacheKey, CategoryVO.class);
        log.info("cachedTree: {}", cachedTree);
        log.info("cachedTree.size(): {}", cachedTree == null ? 0 : cachedTree.size());
        if (cachedTree != null && !cachedTree.isEmpty()) {
            log.info("从缓存获取板块树形结构");
            return cachedTree;
        }
        
        log.info("从数据库获取板块树形结构");
        // 获取所有板块
        List<CommunityCategory> allCategories = this.list();

        // 转换为VO
        List<CategoryVO> categoryVOList = allCategories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 构建树形结构
        List<CategoryVO> result = buildCategoryTree(categoryVOList);
        
        // 缓存结果
        cacheService.setList(cacheKey, result, CacheConstants.CATEGORY_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return result;
    }
    
    /**
     * 获取板块详情
     * @param categoryId 板块ID
     * @return 板块详情
     */
    @Override
    public CategoryVO getCategoryDetail(Long categoryId) {
        // 尝试从缓存获取
        String cacheKey = CacheConstants.CATEGORY_DETAIL_CACHE_KEY_PREFIX + categoryId;
        CategoryVO cachedCategory = cacheService.get(cacheKey, CategoryVO.class);
        if (cachedCategory != null) {
            log.debug("从缓存获取板块详情: {}", categoryId);
            return cachedCategory;
        }
        
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
        
        // 缓存结果
        cacheService.set(cacheKey, categoryVO, CacheConstants.CATEGORY_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
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
        
        // 清除相关缓存
        cacheService.delete(CacheConstants.CATEGORY_TREE_CACHE_KEY);
        cacheService.delete(CacheConstants.ALL_CATEGORIES_CACHE_KEY);
        
        if (category.getParentId() != null) {
            cacheService.delete(CacheConstants.CATEGORY_DETAIL_CACHE_KEY_PREFIX + category.getParentId());
        }
        
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
        
        Long oldParentId = category.getParentId();
        
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
        
        boolean result = this.updateById(category);
        
        // 清除相关缓存
        if (result) {
            cacheService.delete(CacheConstants.CATEGORY_DETAIL_CACHE_KEY_PREFIX + categoryId);
            cacheService.delete(CacheConstants.CATEGORY_TREE_CACHE_KEY);
            cacheService.delete(CacheConstants.ALL_CATEGORIES_CACHE_KEY);
            
            // 如果父级变更，清除旧父级和新父级的缓存
            if (oldParentId != null && !oldParentId.equals(category.getParentId())) {
                cacheService.delete(CacheConstants.CATEGORY_DETAIL_CACHE_KEY_PREFIX + oldParentId);
            }
            
            if (category.getParentId() != null) {
                cacheService.delete(CacheConstants.CATEGORY_DETAIL_CACHE_KEY_PREFIX + category.getParentId());
            }
            
            // 清除用户可见分类缓存
            // 这里使用通配符删除所有用户的可见分类缓存
            // 实际应用中可能需要更精确的缓存失效策略
            cacheService.deleteByPattern(CacheConstants.USER_VISIBLE_CATEGORIES_CACHE_KEY_PREFIX + "*");
        }
        
        return result;
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
        
        // 清除相关缓存
        if (success) {
            cacheService.delete(CacheConstants.CATEGORY_DETAIL_CACHE_KEY_PREFIX + categoryId);
            cacheService.delete(CacheConstants.CATEGORY_TREE_CACHE_KEY);
            cacheService.delete(CacheConstants.ALL_CATEGORIES_CACHE_KEY);
            
            if (category.getParentId() != null) {
                cacheService.delete(CacheConstants.CATEGORY_DETAIL_CACHE_KEY_PREFIX + category.getParentId());
            }
            
            // 清除用户可见分类缓存
            cacheService.deleteByPattern(CacheConstants.USER_VISIBLE_CATEGORIES_CACHE_KEY_PREFIX + "*");
        }
        
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
        // 对于分页数据，只缓存第一页
        if (page == 1) {
            String cacheKey = CacheConstants.ALL_CATEGORIES_CACHE_KEY + ":" + size;
            Page<CategoryVO> cachedPage = cacheService.get(cacheKey, Page.class);
            if (cachedPage != null) {
                log.debug("从缓存获取板块列表");
                return cachedPage;
            }
        }
        
        Page<CommunityCategory> categoryPage = new Page<>(page, size);
        Page<CommunityCategory> resultPage = categoryMapper.selectCategoriesWithTopicCount(categoryPage);
        
        // 转换为VO
        Page<CategoryVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<CategoryVO> records = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(records);
        
        // 缓存第一页数据
        if (page == 1) {
            String cacheKey = CacheConstants.ALL_CATEGORIES_CACHE_KEY + ":" + size;
            cacheService.set(cacheKey, voPage, CacheConstants.CATEGORY_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        }
        
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
        
        boolean result = this.updateById(category);
        
        // 清除相关缓存
        if (result) {
            cacheService.delete(CacheConstants.CATEGORY_DETAIL_CACHE_KEY_PREFIX + categoryId);
            cacheService.delete(CacheConstants.CATEGORY_TREE_CACHE_KEY);
            cacheService.delete(CacheConstants.ALL_CATEGORIES_CACHE_KEY);
        }
        
        return result;
    }
    
    /**
     * 获取用户可见的板块列表
     * @param userId 用户ID
     * @return 板块列表
     */
    @Override
    public List<CategoryVO> getUserVisibleCategories(Long userId) {
        // 尝试从缓存获取
        String cacheKey = CacheConstants.USER_VISIBLE_CATEGORIES_CACHE_KEY_PREFIX + (userId == null ? "anonymous" : userId);
        List<CategoryVO> cachedCategories = cacheService.getList(cacheKey, CategoryVO.class);
        if (cachedCategories != null) {
            log.debug("从缓存获取用户可见板块列表: {}", userId);
            return cachedCategories;
        }
        
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
        List<CategoryVO> result = buildCategoryTree(categoryVOList);
        
        // 缓存结果
        cacheService.setList(cacheKey, result, CacheConstants.USER_CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        return result;
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