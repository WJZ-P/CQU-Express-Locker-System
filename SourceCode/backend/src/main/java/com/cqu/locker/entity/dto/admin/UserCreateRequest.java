package com.cqu.locker.entity.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 创建用户请求
 */
@Data
public class UserCreateRequest {
    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 密码（如果为空则使用默认密码）
     */
    private String password;
    
    /**
     * 角色：0-管理员，1-普通用户，2-快递员
     */
    private Integer role = 1;
    
    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status = 1;
}
