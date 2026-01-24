package com.cqu.locker.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Token校验请求
 */
@Data
public class VerifyTokenRequest {
    
    @NotBlank(message = "token不能为空")
    private String token;
}