package com.csu.unicorp.mapper.recommendation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.recommendation.UserFeature;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户特征Mapper接口
 */
@Mapper
public interface UserFeatureMapper extends BaseMapper<UserFeature> {
    
    /**
     * 根据用户ID获取用户特征
     *
     * @param userId 用户ID
     * @return 用户特征
     */
    @Select("SELECT * FROM user_features WHERE user_id = #{userId}")
    UserFeature selectByUserId(@Param("userId") Integer userId);
    
    /**
     * 获取所有学生用户的特征
     *
     * @return 用户特征列表
     */
    @Select("SELECT uf.* FROM user_features uf " +
            "JOIN users u ON uf.user_id = u.id " +
            "JOIN user_roles ur ON u.id = ur.user_id " +
            "JOIN roles r ON ur.role_id = r.id " +
            "WHERE r.role_name = 'student'")
    List<UserFeature> selectAllStudentFeatures();
    
    /**
     * 获取具有特定技能的用户特征
     *
     * @param skill 技能关键词
     * @return 用户特征列表
     */
    @Select("SELECT * FROM user_features WHERE skills LIKE CONCAT('%', #{skill}, '%')")
    List<UserFeature> selectBySkill(@Param("skill") String skill);
} 