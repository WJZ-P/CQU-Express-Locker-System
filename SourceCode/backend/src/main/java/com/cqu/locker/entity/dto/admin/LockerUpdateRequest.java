package com.cqu.locker.entity.dto.admin;

import lombok.Data;

/**
 * 更新快递柜请求
 */
@Data
public class LockerUpdateRequest {
    /**
     * 快递柜编号
     */
    private String lockerNo;
    
    /**
     * 快递柜名称
     */
    private String name;
    
    /**
     * 位置描述
     */
    private String location;
    
    /**
     * 是否启用：0-禁用，1-启用
     */
    private Integer enabled;
    
    /**
     * 备注
     */
    private String remark;
}
