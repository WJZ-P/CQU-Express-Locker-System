package com.cqu.locker.service.admin;

import com.cqu.locker.entity.BusOrder;
import com.cqu.locker.entity.dto.admin.*;

/**
 * 管理端快递订单服务接口
 */
public interface AdminExpressService {
    
    /**
     * 分页查询快递订单列表
     */
    PageResponse<BusOrder> queryOrders(ExpressQueryRequest request);
    
    /**
     * 获取订单详情
     */
    BusOrder getOrderById(Long id);
    
    /**
     * 删除订单
     */
    void deleteOrder(Long id);
    
    /**
     * 强制取件（管理员操作）
     */
    void forcePickup(Long id);
    
    /**
     * 标记超时
     */
    void markOvertime(Long id);
    
    /**
     * 退回快递
     */
    void returnExpress(Long id);
}
