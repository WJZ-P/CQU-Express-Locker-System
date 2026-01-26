package com.cqu.locker.entity.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建快递柜请求
 */
@Data
public class LockerCreateRequest {
    /**
     * 快递柜编号
     */
    @NotBlank(message = "快递柜编号不能为空")
    private String lockerNo;
    
    /**
     * 快递柜名称
     */
    private String name;
    
    /**
     * 位置描述
     */
    @NotBlank(message = "位置不能为空")
    private String location;
    
    /**
     * 总格口数
     */
    private Integer totalBox = 0;
    
    /**
     * 是否启用：0-禁用，1-启用
     */
    private Integer enabled = 1;
    
    /**
     * 备注
     */
    private String remark;
}
