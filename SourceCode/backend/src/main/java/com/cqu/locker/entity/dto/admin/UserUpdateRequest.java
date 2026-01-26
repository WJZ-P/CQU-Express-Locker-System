package com.cqu.locker.entity.dto.admin;

import lombok.Data;

/**
 * 更新用户请求
 */
@Data
public class UserUpdateRequest {
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 新密码（为空则不修改）
     */
    private String password;
    
    /**
     * 角色：0-管理员，1-普通用户，2-快递员
     */
    private Integer role;
    
    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;
}
