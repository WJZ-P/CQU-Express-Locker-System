package com.cqu.locker.entity.dto.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 格口查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompartmentQueryRequest extends PageRequest {
    /**
     * 所属快递柜ID
     */
    private Long lockerId;
    
    /**
     * 格口状态：0-空闲，1-已存件，2-故障
     */
    private Integer status;
    
    /**
     * 启用状态：0-禁用，1-启用
     */
    private Integer enabled;
    
    /**
     * 格口类型
     */
    private String boxType;
}
