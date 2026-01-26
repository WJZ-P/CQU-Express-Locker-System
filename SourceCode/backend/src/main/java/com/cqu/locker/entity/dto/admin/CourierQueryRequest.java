package com.cqu.locker.entity.dto.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 快递员查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CourierQueryRequest extends PageRequest {
    /**
     * 关键词（搜索姓名、手机号、快递公司）
     */
    private String keyword;
    
    /**
     * 状态：0-离职，1-在职
     */
    private Integer status;
    
    /**
     * 快递公司
     */
    private String company;
}
