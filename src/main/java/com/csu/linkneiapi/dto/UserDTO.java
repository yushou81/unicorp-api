package com.csu.linkneiapi.dto;

import lombok.Data;

/**
 * 用户注册数据传输对象 (Data Transfer Object)
 * 用于接收前端传递的注册信息
 */
@Data
public class UserDTO {

    private String username;

    private String password;

    // 我们可以添加更多的字段，比如 email, verificationCode等
    // DTO的好处是，它与数据库实体解耦，可以根据业务需求灵活变化。
}