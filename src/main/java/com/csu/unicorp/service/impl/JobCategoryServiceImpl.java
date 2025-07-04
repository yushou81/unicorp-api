package com.csu.unicorp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csu.unicorp.common.exception.BusinessException;
import com.csu.unicorp.common.exception.ResourceNotFoundException;
import com.csu.unicorp.dto.job.JobCategoryCreationDTO;
import com.csu.unicorp.dto.job.JobCategoryUpdateDTO;
import com.csu.unicorp.entity.job.JobCategory;
import com.csu.unicorp.mapper.job.JobCategoryMapper;
import com.csu.unicorp.mapper.job.JobCategoryRelationMapper;
import com.csu.unicorp.service.JobCategoryService;
import com.csu.unicorp.vo.JobCategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 岗位分类服务实现类
 */
@Service
@RequiredArgsConstructor
public class JobCategoryServiceImpl extends ServiceImpl<JobCategoryMapper, JobCategory> implements JobCategoryService {

    private final JobCategoryMapper jobCategoryMapper;
    private final JobCategoryRelationMapper jobCategoryRelationMapper;

    @Override
    public List<JobCategoryVO> getRootCategories() {
        List<JobCategory> rootCategories = jobCategoryMapper.selectRootCategories();
        return convertToCategoryVOList(rootCategories);
    }

    @Override
    public List<JobCategoryVO> getChildCategories(Integer parentId) {
        // 检查父分类是否存在
        JobCategory parentCategory = getById(parentId);
        if (parentCategory == null) {
            throw new ResourceNotFoundException("分类不存在");
        }

        List<JobCategory> childCategories = jobCategoryMapper.selectChildCategories(parentId);
        return convertToCategoryVOList(childCategories);
    }

    @Override
    public List<JobCategoryVO> getAllCategories() {
        List<JobCategory> allCategories = list();
        return convertToCategoryVOList(allCategories);
    }

    @Override
    public List<JobCategoryVO> getHierarchicalCategories() {
        // 获取所有顶级分类
        List<JobCategory> rootCategories = jobCategoryMapper.selectRootCategories();
        List<JobCategoryVO> rootCategoryVOs = convertToCategoryVOList(rootCategories);
        
        // 获取所有分类
        List<JobCategory> allCategories = list();
        
        // 递归构建分类树
        for (JobCategoryVO rootCategory : rootCategoryVOs) {
            buildCategoryTree(rootCategory, allCategories);
        }
        
        return rootCategoryVOs;
    }

    /**
     * 递归构建分类树结构
     *
     * @param parent 父级分类VO
     * @param allCategories 所有分类列表
     */
    private void buildCategoryTree(JobCategoryVO parent, List<JobCategory> allCategories) {
        // 查找当前分类的子分类
        List<JobCategory> childCategories = allCategories.stream()
                .filter(category -> parent.getId().equals(category.getParentId()))
                .collect(Collectors.toList());
                
        if (childCategories.isEmpty()) {
            return;
        }
        
        // 转换子分类为VO并设置到父分类中
        List<JobCategoryVO> childCategoryVOs = convertToCategoryVOList(childCategories);
        parent.setChildren(childCategoryVOs);
        
        // 递归处理每个子分类
        for (JobCategoryVO child : childCategoryVOs) {
            buildCategoryTree(child, allCategories);
        }
    }

    @Override
    public JobCategoryVO getCategoryById(Integer id) {
        JobCategory category = getById(id);
        if (category == null) {
            throw new ResourceNotFoundException("分类不存在");
        }
        return convertToCategoryVO(category);
    }

    @Override
    public boolean existsSameNameInSameLevel(String name, Integer parentId) {
        if (parentId == null) {
            // 检查顶级分类是否有同名
            return jobCategoryMapper.countSameNameInRootLevel(name) > 0;
        } else {
            // 检查同一父级下是否有同名分类
            return jobCategoryMapper.countSameNameInSameLevel(name, parentId) > 0;
        }
    }

    @Override
    @Transactional
    public JobCategoryVO createCategory(JobCategoryCreationDTO dto) {
        // 检查父分类是否存在
        if (dto.getParentId() != null) {
            JobCategory parentCategory = getById(dto.getParentId());
            if (parentCategory == null) {
                throw new BusinessException("父分类不存在");
            }
            
            // 检查父分类层级，最多支持三级分类
            if (parentCategory.getLevel() >= 3) {
                throw new BusinessException("最多支持三级分类");
            }
        }
        
        // 检查同级分类中是否已存在同名分类
        if (existsSameNameInSameLevel(dto.getName(), dto.getParentId())) {
            throw new BusinessException("同级分类中已存在同名分类：" + dto.getName());
        }

        // 创建新分类
        JobCategory category = new JobCategory();
        BeanUtils.copyProperties(dto, category);
        
        // 设置层级
        if (dto.getParentId() == null) {
            category.setLevel(1); // 顶级分类
        } else {
            JobCategory parentCategory = getById(dto.getParentId());
            category.setLevel(parentCategory.getLevel() + 1);
        }
        
        LocalDateTime now = LocalDateTime.now();
        category.setCreatedAt(now);
        category.setUpdatedAt(now);
        category.setIsDeleted(false);
        
        save(category);
        return convertToCategoryVO(category);
    }

    @Override
    @Transactional
    public JobCategoryVO updateCategory(Integer id, JobCategoryUpdateDTO dto) {
        JobCategory category = getById(id);
        if (category == null) {
            throw new ResourceNotFoundException("分类不存在");
        }

        // 如果要更新分类名称且名称有变化
        if (dto.getName() != null && !dto.getName().equals(category.getName())) {
            // 检查同级分类中是否已存在同名分类
            if (existsSameNameInSameLevel(dto.getName(), category.getParentId())) {
                throw new BusinessException("同级分类中已存在同名分类：" + dto.getName());
            }
            category.setName(dto.getName());
        }
        
        category.setUpdatedAt(LocalDateTime.now());
        updateById(category);
        
        return convertToCategoryVO(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Integer id) {
        JobCategory category = getById(id);
        if (category == null) {
            throw new ResourceNotFoundException("分类不存在");
        }

        // 检查是否有子分类
        int childCount = jobCategoryMapper.countChildCategories(id);
        if (childCount > 0) {
            throw new BusinessException("该分类下存在子分类，无法删除");
        }

        // 检查是否被岗位引用
        int jobReferenceCount = jobCategoryMapper.countJobReferences(id);
        if (jobReferenceCount > 0) {
            throw new BusinessException("该分类已被岗位引用，无法删除");
        }

        // 删除分类
        removeById(id);
    }

    @Override
    public List<JobCategoryVO> getCategoriesByJobId(Integer jobId) {
        // 获取岗位关联的分类ID列表
        List<Integer> categoryIds = jobCategoryRelationMapper.selectCategoryIdsByJobId(jobId);
        if (categoryIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询分类详情
        List<JobCategory> categories = listByIds(categoryIds);
        return convertToCategoryVOList(categories);
    }

    /**
     * 将实体对象转换为VO对象
     */
    private JobCategoryVO convertToCategoryVO(JobCategory category) {
        JobCategoryVO vo = new JobCategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }

    /**
     * 将实体对象列表转换为VO对象列表
     */
    private List<JobCategoryVO> convertToCategoryVOList(List<JobCategory> categories) {
        return categories.stream()
                .map(this::convertToCategoryVO)
                .collect(Collectors.toList());
    }
} 