package com.cqu.locker.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 用户注册请求
 */
@Data
public class RegisterRequest {
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;
    
    @NotBlank(message = "用户类型不能为空")
    @Pattern(regexp = "^(user|courier)$", message = "用户类型只能是user或courier")
    private String userType;
}
