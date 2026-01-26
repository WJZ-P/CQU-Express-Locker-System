package com.cqu.locker.entity.dto.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 快递订单查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExpressQueryRequest extends PageRequest {
    /**
     * 关键词（搜索快递单号、收件人手机号、取件码）
     */
    private String keyword;
    
    /**
     * 订单状态：0-待取件，1-已取件，2-已超时，3-已退回
     */
    private Integer status;
    
    /**
     * 快递公司
     */
    private String company;
    
    /**
     * 快递柜ID
     */
    private Long lockerId;
    
    /**
     * 快递员ID
     */
    private Long courierId;
    
    /**
     * 开始时间
     */
    private String startTime;
    
    /**
     * 结束时间
     */
    private String endTime;
}
