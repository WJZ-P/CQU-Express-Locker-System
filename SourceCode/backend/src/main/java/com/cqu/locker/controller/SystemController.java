package com.cqu.locker.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cqu.locker.entity.SysConfig;
import com.cqu.locker.service.SysConfigService;
import com.cqu.locker.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统配置控制器
 */
@RestController
@RequestMapping("/api/system")
@CrossOrigin
public class SystemController {

    @Autowired
    private SysConfigService configService;

    @GetMapping("/config")
    public Result<List<SysConfig>> list() {
        return Result.success(configService.list());
    }

    @PostMapping("/config")
    public Result<Boolean> save(@RequestBody SysConfig config) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getParamKey, config.getParamKey());
        SysConfig exist = configService.getOne(wrapper);
        if (exist != null) {
            config.setId(exist.getId());
            return Result.success(configService.updateById(config));
        } else {
            return Result.success(configService.save(config));
        }
    }
}
