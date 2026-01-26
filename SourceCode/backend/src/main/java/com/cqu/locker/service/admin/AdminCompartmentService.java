package com.cqu.locker.service.admin;

import com.cqu.locker.entity.IotBox;
import com.cqu.locker.entity.dto.admin.*;

/**
 * 管理端格口服务接口
 */
public interface AdminCompartmentService {
    
    /**
     * 分页查询格口列表
     */
    PageResponse<IotBox> queryCompartments(CompartmentQueryRequest request);
    
    /**
     * 获取格口详情
     */
    IotBox getCompartmentById(Long id);
    
    /**
     * 创建格口
     */
    IotBox createCompartment(CompartmentCreateRequest request);
    
    /**
     * 更新格口
     */
    IotBox updateCompartment(Long id, CompartmentUpdateRequest request);
    
    /**
     * 删除格口
     */
    void deleteCompartment(Long id);
    
    /**
     * 更新格口启用状态
     */
    void updateCompartmentEnabled(Long id, Integer enabled);
    
    /**
     * 远程开门
     */
    void openDoor(Long id);
    
    /**
     * 标记格口故障
     */
    void markFault(Long id, String reason);
    
    /**
     * 清除格口故障
     */
    void clearFault(Long id);
}
