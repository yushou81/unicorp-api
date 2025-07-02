package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.job.JobCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 岗位分类Mapper接口
 */
@Mapper
public interface JobCategoryMapper extends BaseMapper<JobCategory> {
    
    /**
     * 获取所有顶级分类
     * @return 顶级分类列表
     */
    @Select("SELECT * FROM job_categories WHERE parent_id IS NULL AND is_deleted = 0")
    List<JobCategory> selectRootCategories();
    
    /**
     * 获取指定分类的子分类
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @Select("SELECT * FROM job_categories WHERE parent_id = #{parentId} AND is_deleted = 0")
    List<JobCategory> selectChildCategories(@Param("parentId") Integer parentId);
    
    /**
     * 检查分类是否有子分类
     * @param categoryId 分类ID
     * @return 子分类数量
     */
    @Select("SELECT COUNT(*) FROM job_categories WHERE parent_id = #{categoryId} AND is_deleted = 0")
    int countChildCategories(@Param("categoryId") Integer categoryId);
    
    /**
     * 检查分类是否被岗位引用
     * @param categoryId 分类ID
     * @return 引用该分类的岗位数量
     */
    @Select("SELECT COUNT(*) FROM job_category_relations WHERE category_id = #{categoryId}")
    int countJobReferences(@Param("categoryId") Integer categoryId);
    
    /**
     * 检查同级分类中是否存在同名分类（父级为null的情况）
     * @param name 分类名称
     * @return 同名分类数量
     */
    @Select("SELECT COUNT(*) FROM job_categories WHERE parent_id IS NULL AND name = #{name} AND is_deleted = 0")
    int countSameNameInRootLevel(@Param("name") String name);
    
    /**
     * 检查同级分类中是否存在同名分类（有父级的情况）
     * @param name 分类名称
     * @param parentId 父分类ID
     * @return 同名分类数量
     */
    @Select("SELECT COUNT(*) FROM job_categories WHERE parent_id = #{parentId} AND name = #{name} AND is_deleted = 0")
    int countSameNameInSameLevel(@Param("name") String name, @Param("parentId") Integer parentId);
} 