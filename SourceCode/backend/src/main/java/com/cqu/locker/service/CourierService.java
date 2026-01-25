package com.cqu.locker.service;

import com.cqu.locker.entity.dto.*;

/**
 * 快递员服务接口
 */
public interface CourierService {
    
    /**
     * 获取快递员信息
     * @param courierId 快递员ID
     * @return 快递员信息
     */
    CourierProfileResponse getCourierProfile(Long courierId);
    
    /**
     * 投递快递
     * @param request 投递请求
     * @param courierId 快递员ID
     * @return 投递响应
     */
    DeliverResponse deliver(DeliverRequest request, Long courierId);
    
    /**
     * 查询收件人信息
     * @param phone 收件人手机号
     * @return 收件人信息
     */
    ReceiverInfoResponse queryReceiver(String phone);
    
    /**
     * 获取待揽收列表
     * @param courierId 快递员ID
     * @return 待揽收列表
     */
    PendingCollectResponse getPendingCollectList(Long courierId);
    
    /**
     * 获取待退回列表
     * @param courierId 快递员ID
     * @return 待退回列表
     */
    PendingReturnResponse getPendingReturnList(Long courierId);
    
    /**
     * 开柜操作
     * @param request 开柜请求
     * @param courierId 快递员ID
     */
    void openCompartment(OpenCompartmentRequest request, Long courierId);
}