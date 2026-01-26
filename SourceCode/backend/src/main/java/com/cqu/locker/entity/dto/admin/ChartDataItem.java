package com.cqu.locker.entity.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图表数据项
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataItem {
    /**
     * 日期/名称
     */
    private String name;
    
    /**
     * 数值
     */
    private Long value;
}
