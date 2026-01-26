package com.cqu.locker.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_config")
public class SysConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("param_key")
    private String paramKey;
    
    @TableField("param_value")
    private String paramValue;
    
    private String description;
    
    private LocalDateTime updateTime;
    
    // 兼容configKey/configValue字段名
    public String getConfigKey() {
        return paramKey;
    }
    
    public void setConfigKey(String configKey) {
        this.paramKey = configKey;
    }
    
    public String getConfigValue() {
        return paramValue;
    }
    
    public void setConfigValue(String configValue) {
        this.paramValue = configValue;
    }
}
