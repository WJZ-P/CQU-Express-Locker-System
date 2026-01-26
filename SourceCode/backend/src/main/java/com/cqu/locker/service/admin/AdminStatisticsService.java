package com.cqu.locker.service.admin;

import com.cqu.locker.entity.dto.admin.ChartDataItem;
import com.cqu.locker.entity.dto.admin.DashboardStatsResponse;
import com.cqu.locker.entity.dto.admin.TrendDataResponse;

import java.util.List;
import java.util.Map;

/**
 * 管理端统计服务接口
 */
public interface AdminStatisticsService {
    
    /**
     * 获取仪表盘统计数据
     */
    DashboardStatsResponse getDashboardStats();
    
    /**
     * 获取订单趋势数据（近N天）
     */
    List<ChartDataItem> getOrderTrend(Integer days);
    
    /**
     * 获取订单趋势数据（返回结构化数据）
     */
    TrendDataResponse getOrderTrendData(Integer days);
    
    /**
     * 获取快递公司分布
     */
    List<ChartDataItem> getCompanyDistribution();
    
    /**
     * 获取快递柜使用排行
     */
    List<ChartDataItem> getLockerUsageRanking(Integer limit);
    
    /**
     * 获取每日订单统计
     */
    List<ChartDataItem> getDailyOrderStats(String startDate, String endDate);
    
    /**
     * 获取每小时订单分布
     */
    List<ChartDataItem> getHourlyOrderDistribution();
    
    /**
     * 获取月度订单统计
     */
    Map<String, Object> getMonthlyOrders();
    
    /**
     * 获取格口使用率统计
     */
    Map<String, Object> getBoxUsageStats();
    
    /**
     * 获取用电量统计
     */
    Map<String, Object> getPowerStats();
}
