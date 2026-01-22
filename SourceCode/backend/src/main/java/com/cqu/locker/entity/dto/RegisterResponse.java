package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    
    private String userId;
}
