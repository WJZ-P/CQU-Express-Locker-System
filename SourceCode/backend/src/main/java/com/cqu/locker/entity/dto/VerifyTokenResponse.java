package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token校验响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyTokenResponse {
    
    private boolean valid;
    
    private String token;
    
    private long expiresIn;
    
    private String userId;
    
    private String userType;
}