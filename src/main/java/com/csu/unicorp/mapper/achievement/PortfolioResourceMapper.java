package com.csu.unicorp.mapper.achievement;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.achievement.PortfolioResource;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 作品资源Mapper接口
 */
@Mapper
public interface PortfolioResourceMapper extends BaseMapper<PortfolioResource> {
    
    /**
     * 根据作品ID查询资源列表
     * 
     * @param portfolioItemId 作品项目ID
     * @return 资源列表
     */
    @Select("SELECT * FROM portfolio_resources WHERE portfolio_item_id = #{portfolioItemId} ORDER BY display_order")
    List<PortfolioResource> selectByPortfolioItemId(@Param("portfolioItemId") Integer portfolioItemId);
    
    /**
     * 根据作品ID和资源类型查询资源列表
     * 
     * @param portfolioItemId 作品项目ID
     * @param resourceType 资源类型
     * @return 资源列表
     */
    @Select("SELECT * FROM portfolio_resources WHERE portfolio_item_id = #{portfolioItemId} AND resource_type = #{resourceType} ORDER BY display_order")
    List<PortfolioResource> selectByPortfolioItemIdAndType(@Param("portfolioItemId") Integer portfolioItemId, @Param("resourceType") String resourceType);
    
    /**
     * 删除作品的所有资源
     * 
     * @param portfolioItemId 作品项目ID
     * @return 影响行数
     */
    @Delete("DELETE FROM portfolio_resources WHERE portfolio_item_id = #{portfolioItemId}")
    int deleteByPortfolioItemId(@Param("portfolioItemId") Integer portfolioItemId);
} 