package com.csu.unicorp.vo.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 删除操作结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteResult {
    /**
     * 操作是否成功
     */
    private boolean success;
    
    /**
     * 错误消息，成功时为null
     */
    private String errorMessage;
} 