package com.cqu.locker.entity.dto.admin;

import lombok.Data;

/**
 * 更新格口请求
 */
@Data
public class CompartmentUpdateRequest {
    /**
     * 格口编号
     */
    private String boxNo;
    
    /**
     * 格口类型：small/medium/large
     */
    private String boxType;
    
    /**
     * 启用状态：0-禁用，1-启用
     */
    private Integer enabled;
    
    /**
     * 状态：0-空闲，1-已存件，2-故障
     */
    private Integer status;
    
    /**
     * 故障原因（当status=2时）
     */
    private String faultReason;
}
