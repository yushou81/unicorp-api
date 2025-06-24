package com.csu.linkneiapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.csu.linkneiapi.dto.UserDTO;
import com.csu.linkneiapi.entity.User;

public interface IUserService extends IService<User> {

    /**
     * 用户注册
     * @param userDTO 注册信息
     */
    void register(UserDTO userDTO);
}
