package com.cqu.locker.entity.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 创建快递员请求
 */
@Data
public class CourierCreateRequest {
    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    /**
     * 快递公司
     */
    @NotBlank(message = "快递公司不能为空")
    private String company;
    
    /**
     * 密码（如果为空则使用默认密码）
     */
    private String password;
    
    /**
     * 状态：0-离职，1-在职
     */
    private Integer status = 1;
}
