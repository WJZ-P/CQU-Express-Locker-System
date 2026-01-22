package com.cqu.locker.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 开柜请求
 */
@Data
public class OpenCompartmentRequest {
    
    @NotBlank(message = "快递ID不能为空")
    private String expressId;
}
