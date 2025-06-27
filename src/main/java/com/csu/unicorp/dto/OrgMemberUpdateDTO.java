package com.csu.unicorp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 组织成员更新DTO
 * 用于学校管理员更新教师信息和企业管理员更新企业导师信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgMemberUpdateDTO {
    
    /**
     * 成员昵称
     */
    private String nickname;
    
    /**
     * 成员手机号
     */
    private String phone;
} 