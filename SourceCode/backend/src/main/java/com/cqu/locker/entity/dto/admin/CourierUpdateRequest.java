package com.cqu.locker.entity.dto.admin;

import lombok.Data;

/**
 * 更新快递员请求
 */
@Data
public class CourierUpdateRequest {
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 快递公司
     */
    private String company;
    
    /**
     * 新密码（为空则不修改）
     */
    private String password;
    
    /**
     * 状态：0-离职，1-在职
     */
    private Integer status;
}
