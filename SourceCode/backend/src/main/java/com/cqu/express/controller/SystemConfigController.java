package com.cqu.express.controller;

import com.cqu.express.common.Result;
import com.cqu.express.entity.SystemConfig;
import com.cqu.express.repository.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system")
@CrossOrigin
public class SystemConfigController {

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    @GetMapping("/config")
    public Result<SystemConfig> getConfig() {
        // 获取第一条配置，如果没有则创建默认配置
        return systemConfigRepository.findAll().stream().findFirst()
            .map(Result::success)
            .orElseGet(() -> {
                SystemConfig config = new SystemConfig();
                return Result.success(systemConfigRepository.save(config));
            });
    }
    
    @PutMapping("/config")
    public Result<SystemConfig> updateConfig(@RequestBody SystemConfig config) {
        SystemConfig existing = systemConfigRepository.findAll().stream().findFirst().orElse(null);
        if (existing != null) {
            config.setId(existing.getId());
        }
        return Result.success(systemConfigRepository.save(config));
    }
}
