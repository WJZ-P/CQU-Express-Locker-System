package com.cqu.locker.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqu.locker.entity.IotBox;
import com.cqu.locker.mapper.IotBoxMapper;
import com.cqu.locker.service.IotBoxService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IotBoxServiceImpl extends ServiceImpl<IotBoxMapper, IotBox> implements IotBoxService {

    @Override
    public List<IotBox> getByLockerId(Long lockerId) {
        return this.list(new LambdaQueryWrapper<IotBox>()
                .eq(IotBox::getLockerId, lockerId)
                .orderByAsc(IotBox::getBoxNo));
    }

    @Override
    public boolean openDoor(Long id) {
        // 模拟开门：更新isLocked状态为0（开）
        // 实际场景应调用硬件接口
        IotBox box = new IotBox();
        box.setId(id);
        box.setIsLocked(0); // 0-开
        return this.updateById(box);
    }
}
