package com.csu.linkneiapi.vo;

import lombok.Data;

/**
 * 统一API响应结果视图对象 (View Object)
 * @param <T>
 */
@Data
public class ResultVO<T> {

    /**
     * 业务状态码 (例如: 200代表成功, 500代表业务失败)
     */
    private Integer code;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    // 构造函数
    public ResultVO(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // --- 静态方法，方便快速创建返回对象 ---

    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(200, "操作成功", data);
    }

    public static <T> ResultVO<T> success() {
        return success(null);
    }

    public static <T> ResultVO<T> error(Integer code, String message) {
        return new ResultVO<>(code, message, null);
    }

    public static <T> ResultVO<T> error() {
        return error(500, "操作失败");
    }
}
