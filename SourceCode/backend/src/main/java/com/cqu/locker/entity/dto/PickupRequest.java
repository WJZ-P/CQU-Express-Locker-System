package com.cqu.locker.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 取件请求
 */
@Data
public class PickupRequest {
    
    @NotBlank(message = "快递ID不能为空")
    private String expressId;
    
    @NotBlank(message = "取件码不能为空")
    private String pickupCode;
}
