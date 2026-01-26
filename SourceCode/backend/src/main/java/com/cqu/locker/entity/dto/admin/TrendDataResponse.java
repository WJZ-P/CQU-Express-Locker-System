package com.cqu.locker.entity.dto.admin;

import lombok.Data;
import java.util.List;

/**
 * 趋势数据响应
 */
@Data
public class TrendDataResponse {
    /**
     * 日期列表
     */
    private List<String> dates;
    
    /**
     * 入柜数据
     */
    private List<Long> depositData;
    
    /**
     * 取件数据
     */
    private List<Long> pickupData;
    
    /**
     * 已使用格口数
     */
    private Long usedCount;
    
    /**
     * 空闲格口数
     */
    private Long availableCount;
}
