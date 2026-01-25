package com.cqu.locker.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 收件人信息响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiverInfoResponse {
    
    private String userId;
    private String name;
    private String phone;
    private DefaultLocker defaultLocker;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DefaultLocker {
        private String lockerId;
        private String lockerName;
    }
}