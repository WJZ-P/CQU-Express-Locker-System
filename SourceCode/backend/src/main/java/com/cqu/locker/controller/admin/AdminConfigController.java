package com.cqu.locker.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cqu.locker.entity.SysConfig;
import com.cqu.locker.mapper.SysConfigMapper;
import com.cqu.locker.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 管理端-系统配置控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin/config")
@CrossOrigin
public class AdminConfigController {
    
    @Autowired
    private SysConfigMapper configMapper;
    
    /**
     * 获取所有配置
     */
    @GetMapping("/list")
    public Result<List<SysConfig>> list() {
        try {
            List<SysConfig> configs = configMapper.selectList(null);
            return Result.success(configs);
        } catch (Exception e) {
            log.error("获取配置列表失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取单个配置
     */
    @GetMapping("/{key}")
    public Result<SysConfig> getByKey(@PathVariable String key) {
        try {
            LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysConfig::getConfigKey, key);
            SysConfig config = configMapper.selectOne(wrapper);
            return Result.success(config);
        } catch (Exception e) {
            log.error("获取配置失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新单个配置
     */
    @PutMapping("/{key}")
    public Result<Void> update(@PathVariable String key, @RequestBody Map<String, String> body) {
        try {
            String value = body.get("value");
            
            LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysConfig::getConfigKey, key);
            SysConfig config = configMapper.selectOne(wrapper);
            
            if (config != null) {
                config.setConfigValue(value);
                config.setUpdateTime(LocalDateTime.now());
                configMapper.updateById(config);
            } else {
                config = new SysConfig();
                config.setConfigKey(key);
                config.setConfigValue(value);
                config.setUpdateTime(LocalDateTime.now());
                configMapper.insert(config);
            }
            
            return Result.success();
        } catch (Exception e) {
            log.error("更新配置失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量更新配置
     */
    @PostMapping("/batch")
    public Result<Void> batchUpdate(@RequestBody List<Map<String, String>> configs) {
        try {
            for (Map<String, String> item : configs) {
                String key = item.get("configKey");
                String value = item.get("configValue");
                
                if (key == null || key.isEmpty()) {
                    continue;
                }
                
                LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(SysConfig::getConfigKey, key);
                SysConfig config = configMapper.selectOne(wrapper);
                
                if (config != null) {
                    config.setConfigValue(value);
                    config.setUpdateTime(LocalDateTime.now());
                    configMapper.updateById(config);
                } else {
                    config = new SysConfig();
                    config.setConfigKey(key);
                    config.setConfigValue(value);
                    config.setUpdateTime(LocalDateTime.now());
                    configMapper.insert(config);
                }
            }
            
            return Result.success();
        } catch (Exception e) {
            log.error("批量更新配置失败", e);
            return Result.error(e.getMessage());
        }
    }
}
