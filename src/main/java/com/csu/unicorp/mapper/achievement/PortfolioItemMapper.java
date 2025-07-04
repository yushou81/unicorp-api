package com.csu.unicorp.mapper.achievement;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.achievement.PortfolioItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 作品项目Mapper接口
 */
@Mapper
public interface PortfolioItemMapper extends BaseMapper<PortfolioItem> {
    
    /**
     * 根据用户ID查询作品列表
     * 
     * @param userId 用户ID
     * @return 作品列表
     */
    @Select("SELECT * FROM portfolio_items WHERE user_id = #{userId} AND is_deleted = 0")
    List<PortfolioItem> selectByUserId(@Param("userId") Integer userId);
    
    /**
     * 分页查询用户的作品列表
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @return 作品分页列表
     */
    @Select("SELECT * FROM portfolio_items WHERE user_id = #{userId} AND is_deleted = 0")
    Page<PortfolioItem> selectPageByUserId(Page<PortfolioItem> page, @Param("userId") Integer userId);
    
    /**
     * 分页查询公开的作品列表
     * 
     * @param page 分页参数
     * @return 公开作品分页列表
     */
    @Select("SELECT * FROM portfolio_items WHERE is_public = true AND is_deleted = 0")
    Page<PortfolioItem> selectPublicPage(Page<PortfolioItem> page);
    
    /**
     * 根据分类分页查询公开的作品列表
     * 
     * @param page 分页参数
     * @param category 作品分类
     * @return 公开作品分页列表
     */
    @Select("SELECT * FROM portfolio_items WHERE category = #{category} AND is_public = true AND is_deleted = 0")
    Page<PortfolioItem> selectPublicPageByCategory(Page<PortfolioItem> page, @Param("category") String category);
    
    /**
     * 增加作品查看次数
     * 
     * @param id 作品ID
     * @return 影响行数
     */
    @Update("UPDATE portfolio_items SET view_count = view_count + 1 WHERE id = #{id}")
    int increaseViewCount(@Param("id") Integer id);
    
    /**
     * 增加作品点赞数
     * 
     * @param id 作品ID
     * @return 影响行数
     */
    @Update("UPDATE portfolio_items SET like_count = like_count + 1 WHERE id = #{id}")
    int increaseLikeCount(@Param("id") Integer id);
} 