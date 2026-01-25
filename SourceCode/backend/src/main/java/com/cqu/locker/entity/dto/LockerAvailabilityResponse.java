package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 快递柜可用格口信息响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LockerAvailabilityResponse {
    
    /**
     * 快递柜ID
     */
    private Long lockerId;
    
    /**
     * 快递柜名称
     */
    private String lockerName;
    
    /**
     * 小格口空闲数量
     */
    private Integer smallCount;
    
    /**
     * 中格口空闲数量
     */
    private Integer mediumCount;
    
    /**
     * 大格口空闲数量
     */
    private Integer largeCount;
}
