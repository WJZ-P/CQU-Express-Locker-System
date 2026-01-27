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
     * 仅通过取件码取件（用于寄存物品共享场景）
     * @param request 取件码请求
     * @return 取件响应
     */
    PickupResponse pickupByCode(PickupByCodeRequest request);
    
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
    
    /**
     * 创建寄存订单
     * @param request 寄存请求
     * @param userId 用户ID
     * @return 寄存响应
     */
    CreateStorageResponse createStorage(CreateStorageRequest request, Long userId);
    
    /**
     * 获取寄存列表
     * @param userId 用户ID
     * @return 寄存列表
     */
    StorageListResponse getStorageList(Long userId);
    
    /**
     * 获取历史记录
     * @param userId 用户ID
     * @param type 记录类型：all-全部, pickup-取件, send-寄件, storage-寄存
     * @param page 页码
     * @param pageSize 每页大小
     * @return 历史记录列表
     */
    HistoryResponse getHistory(Long userId, String type, Integer page, Integer pageSize);
    
    /**
     * 获取快递柜可用格口信息
     * @param lockerId 快递柜ID
     * @return 快递柜可用格口信息
     */
    LockerAvailabilityResponse getLockerAvailability(Long lockerId);
}
