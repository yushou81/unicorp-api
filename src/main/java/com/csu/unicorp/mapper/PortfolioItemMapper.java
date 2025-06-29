package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.PortfolioItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 作品集项目Mapper接口
 */
@Mapper
public interface PortfolioItemMapper extends BaseMapper<PortfolioItem> {
    
    /**
     * 根据用户ID查询所有作品集项目
     * 
     * @param userId 用户ID
     * @return 作品集项目列表
     */
    @Select("SELECT * FROM portfolio_items WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY created_at DESC")
    List<PortfolioItem> selectByUserId(Integer userId);
} 