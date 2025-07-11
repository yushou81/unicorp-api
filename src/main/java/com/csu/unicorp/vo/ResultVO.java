package com.csu.unicorp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用响应VO类，用于规范API响应格式
 */
@Data
@NoArgsConstructor
@Schema(description = "统一API响应结果")
public class ResultVO<T> {

    /**
     * 状态码: 200-成功，400-客户端错误，500-服务端错误
     */
    @Schema(description = "业务状态码", example = "200")
    private Integer code;

    /**
     * 响应消息
     */
    @Schema(description = "响应信息", example = "操作成功")
    private String message;

    /**
     * 响应数据
     */
    @Schema(description = "响应数据")
    private T data;
    
    /**
     * 聊天会话中的对方用户ID（仅在聊天相关接口中使用）
     */
    @Schema(description = "聊天会话中的对方用户ID")
    private Long otherUserId;
    
    
    /**
     * 构造函数
     * 
     * @param code 状态码
     * @param message 响应信息
     * @param data 响应数据
     */
    public ResultVO(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 创建成功响应
     * 
     * @param <T> 数据类型
     * @param message 成功消息
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> ResultVO<T> success(String message, T data) {
        return new ResultVO<>(200, message, data);
    }
    
    /**
     * 创建成功响应（无数据）
     * 
     * @param <T> 数据类型
     * @param message 成功消息
     * @return 成功响应对象
     */
    public static <T> ResultVO<T> success(String message) {
        return new ResultVO<>(200, message, null);
    }
    
    /**
     * 创建成功响应（使用默认成功消息）
     * 
     * @param <T> 数据类型
     * @param data 响应数据
     * @return 成功响应对象
     */
    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(200, "操作成功", data);
    }
    
    /**
     * 创建失败响应（客户端错误）
     * 
     * @param <T> 数据类型
     * @param message 错误消息
     * @return 失败响应对象
     */
    public static <T> ResultVO<T> error(String message) {
        return new ResultVO<>(400, message, null);
    }
    
    /**
     * 创建失败响应（指定状态码）
     * 
     * @param <T> 数据类型
     * @param code 错误状态码
     * @param message 错误消息
     * @return 失败响应对象
     */
    public static <T> ResultVO<T> error(Integer code, String message) {
        return new ResultVO<>(code, message, null);
    }
    
    /**
     * 创建失败响应（指定状态码），与error方法功能相同，提供更语义化的命名
     * 
     * @param <T> 数据类型
     * @param code 错误状态码
     * @param message 错误消息
     * @return 失败响应对象
     */
    public static <T> ResultVO<T> fail(Integer code, String message) {
        return new ResultVO<>(code, message, null);
    }
    
    /**
     * 创建失败响应（服务端错误）
     * 
     * @param <T> 数据类型
     * @param message 错误消息
     * @return 失败响应对象
     */
    public static <T> ResultVO<T> serverError(String message) {
        return new ResultVO<>(500, message, null);
    }
    
    /**
     * 设置聊天会话中的对方用户ID
     * 
     * @param otherUserId 对方用户ID
     * @return 当前对象（链式调用）
     */
    public ResultVO<T> setOtherUserId(Long otherUserId) {
        this.otherUserId = otherUserId;
        return this;
    }
}
