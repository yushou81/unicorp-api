package com.csu.linkneiapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.csu.linkneiapi.dto.LoginDTO;
import com.csu.linkneiapi.dto.UserDTO;
import com.csu.linkneiapi.entity.User;
import com.csu.linkneiapi.vo.JwtResponseVO;

public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userDTO 注册信息
     */
    void register(UserDTO userDTO);
    
    /**
     * 用户登录
     * @param loginDTO 登录信息
     * @return JWT响应信息，包含token等
     */
    JwtResponseVO login(LoginDTO loginDTO);
}
