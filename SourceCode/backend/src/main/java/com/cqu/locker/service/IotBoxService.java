package com.cqu.locker.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cqu.locker.entity.IotBox;
import java.util.List;

public interface IotBoxService extends IService<IotBox> {
    /**
     * 获取指定快递柜的格口列表
     */
    List<IotBox> getByLockerId(Long lockerId);
    
    /**
     * 远程开门
     */
    boolean openDoor(Long id);
}
