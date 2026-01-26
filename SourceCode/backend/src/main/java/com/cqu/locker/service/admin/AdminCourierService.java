package com.cqu.locker.service.admin;

import com.cqu.locker.entity.SysCourier;
import com.cqu.locker.entity.dto.admin.*;

/**
 * 管理端快递员服务接口
 */
public interface AdminCourierService {
    
    /**
     * 分页查询快递员列表
     */
    PageResponse<SysCourier> queryCouriers(CourierQueryRequest request);
    
    /**
     * 获取快递员详情
     */
    SysCourier getCourierById(Long id);
    
    /**
     * 创建快递员（同时创建用户账号）
     */
    SysCourier createCourier(CourierCreateRequest request);
    
    /**
     * 更新快递员
     */
    SysCourier updateCourier(Long id, CourierUpdateRequest request);
    
    /**
     * 删除快递员
     */
    void deleteCourier(Long id);
    
    /**
     * 更新快递员状态（在职/离职）
     */
    void updateCourierStatus(Long id, Integer status);
}
