package com.cqu.locker.entity.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建格口请求
 */
@Data
public class CompartmentCreateRequest {
    /**
     * 所属快递柜ID
     */
    @NotNull(message = "快递柜ID不能为空")
    private Long lockerId;
    
    /**
     * 格口编号
     */
    private String boxNo;
    
    /**
     * 格口类型：small/medium/large
     */
    private String boxType = "medium";
    
    /**
     * 是否启用：0-禁用，1-启用
     */
    private Integer enabled = 1;
}
