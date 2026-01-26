package com.cqu.locker.service.admin;

import com.cqu.locker.entity.IotLocker;
import com.cqu.locker.entity.dto.admin.*;

/**
 * 管理端快递柜服务接口
 */
public interface AdminLockerService {
    
    /**
     * 分页查询快递柜列表
     */
    PageResponse<IotLocker> queryLockers(LockerQueryRequest request);
    
    /**
     * 获取快递柜详情
     */
    IotLocker getLockerById(Long id);
    
    /**
     * 创建快递柜
     */
    IotLocker createLocker(LockerCreateRequest request);
    
    /**
     * 更新快递柜
     */
    IotLocker updateLocker(Long id, LockerUpdateRequest request);
    
    /**
     * 删除快递柜
     */
    void deleteLocker(Long id);
    
    /**
     * 更新快递柜启用状态
     */
    void updateLockerEnabled(Long id, Integer enabled);
}
