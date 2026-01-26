package com.cqu.locker.entity.dto.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryRequest extends PageRequest {
    /**
     * 关键词（搜索用户名、昵称、手机号）
     */
    private String keyword;
    
    /**
     * 用户状态：0-禁用，1-正常
     */
    private Integer status;
    
    /**
     * 角色：0-管理员，1-普通用户，2-快递员
     */
    private Integer role;
}
