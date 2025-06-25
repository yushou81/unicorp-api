package com.csu.linkneiapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.csu.linkneiapi.dto.LoginDTO;
import com.csu.linkneiapi.dto.ProfileUpdateDTO;
import com.csu.linkneiapi.dto.UserDTO;
import com.csu.linkneiapi.entity.User;
import com.csu.linkneiapi.vo.JwtResponseVO;
import com.csu.linkneiapi.vo.UserProfileVO;

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
    
    /**
     * 获取用户个人资料
     * @param username 用户名
     * @return 用户个人资料VO
     */
    UserProfileVO getUserProfile(String username);
    
    /**
     * 更新用户个人资料
     * @param username 用户名
     * @param profileUpdateDTO 更新的个人资料信息
     */
    void updateUserProfile(String username, ProfileUpdateDTO profileUpdateDTO);
}
