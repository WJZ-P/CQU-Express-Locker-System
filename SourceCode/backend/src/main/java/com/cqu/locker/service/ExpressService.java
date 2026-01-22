package com.cqu.locker.service;

import com.cqu.locker.entity.dto.*;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 快递服务接口
 */
public interface ExpressService {
    
    /**
     * 获取待取快递列表
     * @param userId 用户ID
     * @return 待取快递列表
     */
    ExpressPendingResponse getPendingList(Long userId);
    
    /**
     * 获取快递详情
     * @param expressId 快递ID
     * @param userId 用户ID
     * @return 快递详情
     */
    ExpressDetailResponse getExpressDetail(String expressId, Long userId);
    
    /**
     * 取件（验证取件码）
     * @param request 取件请求
     * @param userId 用户ID
     * @return 取件响应
     */
    PickupResponse pickup(PickupRequest request, Long userId);
    
    /**
     * 开柜（已验证后再次开柜）
     * @param request 开柜请求
     * @param userId 用户ID
     */
    void openCompartment(OpenCompartmentRequest request, Long userId);
    
    /**
     * 寄件
     * @param request 寄件请求
     * @param userId 用户ID
     * @return 寄件响应
     */
    SendExpressResponse sendExpress(SendExpressRequest request, Long userId);
}
