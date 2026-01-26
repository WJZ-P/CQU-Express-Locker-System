package com.cqu.locker.entity.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 仪表盘统计响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    /**
     * 今日新增订单数
     */
    private Long todayOrders;
    
    /**
     * 待取件数量
     */
    private Long pendingPickup;
    
    /**
     * 超时件数量
     */
    private Long overtimeOrders;
    
    /**
     * 在线快递柜数量
     */
    private Long onlineLockers;
    
    /**
     * 总快递柜数量
     */
    private Long totalLockers;
    
    /**
     * 空闲格口数量
     */
    private Long availableCompartments;
    
    /**
     * 总格口数量
     */
    private Long totalCompartments;
    
    /**
     * 用户总数
     */
    private Long totalUsers;
    
    /**
     * 快递员总数
     */
    private Long totalCouriers;
    
    /**
     * 今日订单量同比（百分比）
     */
    private BigDecimal orderGrowthRate;
    
    /**
     * 快递柜在线率（百分比）
     */
    private BigDecimal lockerOnlineRate;
    
    /**
     * 格口使用率（百分比）
     */
    private BigDecimal compartmentUsageRate;
}
