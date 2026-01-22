package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String token;
    
    private String userId;
    
    private String userType;
    
    private String nickname;
}
