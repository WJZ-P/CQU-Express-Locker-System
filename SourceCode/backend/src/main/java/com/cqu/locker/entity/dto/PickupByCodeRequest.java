package com.cqu.locker.entity.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 仅通过取件码取件请求
 */
@Data
public class PickupByCodeRequest {
    
    @NotBlank(message = "取件码不能为空")
    private String pickupCode;
}
