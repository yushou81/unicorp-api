package com.csu.unicorp.service;

import com.csu.unicorp.dto.job.JobCategoryCreationDTO;
import com.csu.unicorp.dto.job.JobCategoryUpdateDTO;
import com.csu.unicorp.vo.JobCategoryVO;

import java.util.List;

/**
 * 岗位分类服务接口
 */
public interface JobCategoryService {

    /**
     * 获取所有顶级分类
     *
     * @return 顶级分类列表
     */
    List<JobCategoryVO> getRootCategories();

    /**
     * 获取指定分类的子分类
     *
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<JobCategoryVO> getChildCategories(Integer parentId);

    /**
     * 获取所有分类（平铺结构）
     *
     * @return 所有分类列表
     */
    List<JobCategoryVO> getAllCategories();

    /**
     * 获取所有分类（层级结构）
     *
     * @return 层级结构的分类列表
     */
    List<JobCategoryVO> getHierarchicalCategories();

    /**
     * 获取分类详情
     *
     * @param id 分类ID
     * @return 分类详情
     */
    JobCategoryVO getCategoryById(Integer id);

    /**
     * 创建分类
     *
     * @param dto 分类创建DTO
     * @return 创建后的分类
     */
    JobCategoryVO createCategory(JobCategoryCreationDTO dto);

    /**
     * 检查同级分类中是否已存在指定名称的分类
     *
     * @param name     分类名称
     * @param parentId 父分类ID，如果是顶级分类则为null
     * @return 是否已存在同名分类
     */
    boolean existsSameNameInSameLevel(String name, Integer parentId);

    /**
     * 更新分类
     *
     * @param id  分类ID
     * @param dto 分类更新DTO
     * @return 更新后的分类
     */
    JobCategoryVO updateCategory(Integer id, JobCategoryUpdateDTO dto);

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void deleteCategory(Integer id);

    /**
     * 根据岗位ID获取关联的分类
     *
     * @param jobId 岗位ID
     * @return 分类列表
     */
    List<JobCategoryVO> getCategoriesByJobId(Integer jobId);
} 