package com.cqu.locker.controller;

import com.cqu.locker.entity.IotBox;
import com.cqu.locker.entity.IotLocker;
import com.cqu.locker.service.IotBoxService;
import com.cqu.locker.service.IotLockerService;
import com.cqu.locker.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 快递柜管理控制器
 * 提供快递柜的增删改查及状态控制接口
 */
@RestController
@RequestMapping("/api/v1/locker")
@CrossOrigin // 允许跨域请求，方便前端开发
public class LockerController {

    @Autowired
    private IotLockerService lockerService;
    
    @Autowired
    private IotBoxService boxService;

    /**
     * 获取所有快递柜列表
     * @return 快递柜列表数据
     */
    @GetMapping("/list")
    public Result<List<IotLocker>> list() {
        return Result.success(lockerService.list());
    }

    /**
     * 根据ID获取快递柜详情
     * @param id 快递柜ID
     * @return 快递柜详细信息
     */
    @GetMapping("/{id}")
    public Result<IotLocker> getById(@PathVariable Long id) {
        return Result.success(lockerService.getById(id));
    }
    
    /**
     * 新增快递柜
     * @param locker 快递柜实体信息
     * @return 操作结果
     */
    @PostMapping
    public Result<Boolean> save(@RequestBody IotLocker locker) {
        return Result.success(lockerService.save(locker));
    }
    
    /**
     * 更新快递柜状态
     * @param id 快递柜ID
     * @param status 新状态（0-离线，1-在线）
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        IotLocker locker = new IotLocker();
        locker.setId(id);
        locker.setStatus(status);
        return Result.success(lockerService.updateById(locker));
    }
    
    /**
     * 删除快递柜
     * @param id 快递柜ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> remove(@PathVariable Long id) {
        return Result.success(lockerService.removeById(id));
    }
    
    // --- 格口管理接口 ---

    /**
     * 获取快递柜格口列表
     */
    @GetMapping("/{id}/compartments")
    public Result<List<IotBox>> getCompartments(@PathVariable Long id) {
        return Result.success(boxService.getByLockerId(id));
    }

    /**
     * 远程开门
     */
    @PostMapping("/{lockerId}/compartments/{compartmentId}/open")
    public Result<Boolean> openCompartment(@PathVariable Long lockerId, @PathVariable Long compartmentId) {
        return Result.success(boxService.openDoor(compartmentId));
    }
    
    /**
     * 更新格口状态
     */
    @PutMapping("/{lockerId}/compartments/{compartmentId}/status")
    public Result<Boolean> updateCompartmentStatus(@PathVariable Long lockerId, @PathVariable Long compartmentId, @RequestParam Integer status) {
        IotBox box = new IotBox();
        box.setId(compartmentId);
        box.setStatus(status);
        return Result.success(boxService.updateById(box));
    }
}
