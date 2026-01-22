package com.cqu.locker.utils;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码工具类
 * 使用内存存储，实际生产环境建议使用Redis
 */
@Slf4j
@Component
public class VerifyCodeUtil {
    
    /**
     * 验证码缓存：key为"手机号_类型"，value为验证码
     */
    private static final Map<String, CodeInfo> CODE_CACHE = new ConcurrentHashMap<>();
    
    /**
     * 验证码有效期：5分钟（毫秒）
     */
    private static final long EXPIRE_TIME = 5 * 60 * 1000;
    
    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 6;
    
    /**
     * 生成验证码
     * @param phone 手机号
     * @param type 类型（register/reset）
     * @return 验证码
     */
    public String generateCode(String phone, String type) {
        String code = RandomUtil.randomNumbers(CODE_LENGTH);
        String key = buildKey(phone, type);
        CODE_CACHE.put(key, new CodeInfo(code, System.currentTimeMillis()));
        
        // 实际生产环境应调用短信接口发送验证码
        log.info("发送验证码：手机号={}, 类型={}, 验证码={}", phone, type, code);
        
        return code;
    }
    
    /**
     * 验证验证码
     * @param phone 手机号
     * @param type 类型
     * @param code 验证码
     * @return 是否正确
     */
    public boolean verifyCode(String phone, String type, String code) {
        String key = buildKey(phone, type);
        CodeInfo codeInfo = CODE_CACHE.get(key);
        
        if (codeInfo == null) {
            log.warn("验证码不存在：手机号={}, 类型={}", phone, type);
            return false;
        }
        
        // 检查是否过期
        if (System.currentTimeMillis() - codeInfo.getTimestamp() > EXPIRE_TIME) {
            CODE_CACHE.remove(key);
            log.warn("验证码已过期：手机号={}, 类型={}", phone, type);
            return false;
        }
        
        // 验证成功后删除验证码
        if (codeInfo.getCode().equals(code)) {
            CODE_CACHE.remove(key);
            return true;
        }
        
        return false;
    }
    
    /**
     * 构建缓存key
     */
    private String buildKey(String phone, String type) {
        return phone + "_" + type;
    }
    
    /**
     * 验证码信息
     */
    private static class CodeInfo {
        private final String code;
        private final long timestamp;
        
        public CodeInfo(String code, long timestamp) {
            this.code = code;
            this.timestamp = timestamp;
        }
        
        public String getCode() {
            return code;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
}
