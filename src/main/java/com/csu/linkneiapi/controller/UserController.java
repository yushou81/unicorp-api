package com.csu.linkneiapi.controller;

import com.csu.linkneiapi.dto.UserDTO;
import com.csu.linkneiapi.service.IUserService;
import com.csu.linkneiapi.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user") // 结合yml中的context-path，完整路径是 /api/user
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/register") // 完整路径是 /api/user/register
    public ResultVO<?> register(@RequestBody UserDTO userDTO) {
        try {
            userService.register(userDTO);
            // 注册成功，返回成功信息
            return ResultVO.success("注册成功！");
        } catch (RuntimeException e) {
            // 捕获Service层抛出的异常（如用户名已存在）
            // 返回失败信息
            return ResultVO.error(400, e.getMessage());
        }
    }
}
