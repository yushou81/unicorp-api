package com.csu.unicorp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csu.unicorp.entity.UserVerification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户实名认证Mapper接口
 */
@Mapper
public interface UserVerificationMapper extends BaseMapper<UserVerification> {
} 