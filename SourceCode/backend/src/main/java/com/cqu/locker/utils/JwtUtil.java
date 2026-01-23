package com.cqu.locker.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
public class JwtUtil {
    
    /**
     * 密钥
     */
    private static final String SECRET = "CQU-Express-Locker-System-Secret-Key-2026";
    
    /**
     * 过期时间：7天（毫秒）
     */
    public static long EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000;
    
    /**
     * 生成token
     * @param userId 用户ID
     * @param userType 用户类型
     * @return token
     */
    public static String generateToken(Long userId, String userType) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", userId);
        payload.put("userType", userType);
        payload.put("exp", DateUtil.offsetMillisecond(new Date(), (int) EXPIRE_TIME));
        
        return JWTUtil.createToken(payload, SECRET.getBytes());
    }
    
    /**
     * 验证token
     * @param token token
     * @return 是否有效
     */
    public static boolean verify(String token) {
        try {
            return JWTUtil.verify(token, SECRET.getBytes());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 解析token获取用户ID
     * @param token token
     * @return 用户ID
     */
    public static Long getUserId(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        Object userId = jwt.getPayload("userId");
        
        // 使用更健壮的方式获取userId
        String userIdStr = String.valueOf(userId);
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 解析token获取用户类型
     * @param token token
     * @return 用户类型
     */
    public static String getUserType(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        return (String) jwt.getPayload("userType");
    }
}
