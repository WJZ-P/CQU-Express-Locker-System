package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户信息响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    
    private String userId;
    
    private String phone;
    
    private String nickname;
    
    private List<BindLocker> bindLockers;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BindLocker {
        private String lockerId;
        private String lockerName;
        private String address;
    }
}
