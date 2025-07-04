package com.csu.unicorp.mapper.community;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csu.unicorp.entity.community.CommunityUserRelation;

/**
 * 社区用户关系Mapper接口
 */
public interface CommunityUserRelationMapper extends BaseMapper<CommunityUserRelation> {
    
    /**
     * 检查用户关系是否存在
     * @param userId 用户ID
     * @param targetId 目标用户ID
     * @param relationType 关系类型
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) FROM community_user_relation WHERE user_id = #{userId} AND target_id = #{targetId} AND relation_type = #{relationType}")
    int checkRelationExists(@Param("userId") Long userId, @Param("targetId") Long targetId, @Param("relationType") String relationType);
    
    /**
     * 获取用户关注列表
     * @param page 分页参数
     * @param userId 用户ID
     * @return 关注列表
     */
    @Select("SELECT * FROM community_user_relation WHERE user_id = #{userId} AND relation_type = 'FOLLOW' ORDER BY created_at DESC")
    Page<CommunityUserRelation> selectFollowings(Page<CommunityUserRelation> page, @Param("userId") Long userId);
    
    /**
     * 获取用户粉丝列表
     * @param page 分页参数
     * @param targetId 目标用户ID
     * @return 粉丝列表
     */
    @Select("SELECT * FROM community_user_relation WHERE target_id = #{targetId} AND relation_type = 'FOLLOW' ORDER BY created_at DESC")
    Page<CommunityUserRelation> selectFollowers(Page<CommunityUserRelation> page, @Param("targetId") Long targetId);
    
    /**
     * 获取用户拉黑列表
     * @param page 分页参数
     * @param userId 用户ID
     * @return 拉黑列表
     */
    @Select("SELECT * FROM community_user_relation WHERE user_id = #{userId} AND relation_type = 'BLOCK' ORDER BY created_at DESC")
    Page<CommunityUserRelation> selectBlocks(Page<CommunityUserRelation> page, @Param("userId") Long userId);
    
    /**
     * 删除用户关系
     * @param userId 用户ID
     * @param targetId 目标用户ID
     * @param relationType 关系类型
     * @return 影响行数
     */
    @Delete("DELETE FROM community_user_relation WHERE user_id = #{userId} AND target_id = #{targetId} AND relation_type = #{relationType}")
    int deleteRelation(@Param("userId") Long userId, @Param("targetId") Long targetId, @Param("relationType") String relationType);
    
    /**
     * 获取用户关注的用户ID列表
     * @param userId 用户ID
     * @return 关注的用户ID列表
     */
    @Select("SELECT target_id FROM community_user_relation WHERE user_id = #{userId} AND relation_type = 'FOLLOW'")
    List<Long> selectFollowingIds(@Param("userId") Long userId);
    
    /**
     * 获取用户粉丝的用户ID列表
     * @param targetId 目标用户ID
     * @return 粉丝的用户ID列表
     */
    @Select("SELECT user_id FROM community_user_relation WHERE target_id = #{targetId} AND relation_type = 'FOLLOW'")
    List<Long> selectFollowerIds(@Param("targetId") Long targetId);
    
    /**
     * 获取用户拉黑的用户ID列表
     * @param userId 用户ID
     * @return 拉黑的用户ID列表
     */
    @Select("SELECT target_id FROM community_user_relation WHERE user_id = #{userId} AND relation_type = 'BLOCK'")
    List<Long> selectBlockIds(@Param("userId") Long userId);
    
    /**
     * 获取用户关注数量
     * @param userId 用户ID
     * @return 关注数量
     */
    @Select("SELECT COUNT(*) FROM community_user_relation WHERE user_id = #{userId} AND relation_type = 'FOLLOW'")
    int countFollowings(@Param("userId") Long userId);
    
    /**
     * 获取用户粉丝数量
     * @param targetId 目标用户ID
     * @return 粉丝数量
     */
    @Select("SELECT COUNT(*) FROM community_user_relation WHERE target_id = #{targetId} AND relation_type = 'FOLLOW'")
    int countFollowers(@Param("targetId") Long targetId);
} 