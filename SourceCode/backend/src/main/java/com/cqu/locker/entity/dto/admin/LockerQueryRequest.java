package com.cqu.locker.entity.dto.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 快递柜查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LockerQueryRequest extends PageRequest {
    /**
     * 关键词（搜索名称、编号、位置）
     */
    private String keyword;
    
    /**
     * 在线状态：0-离线，1-在线
     */
    private Integer status;
    
    /**
     * 启用状态：0-禁用，1-启用
     */
    private Integer enabled;
}
