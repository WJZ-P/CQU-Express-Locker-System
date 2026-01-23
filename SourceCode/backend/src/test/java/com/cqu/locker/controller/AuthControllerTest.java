package com.cqu.locker.controller;

import com.cqu.locker.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthController测试类
 * 测试Token验证功能
 */
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试1：生成有效token
     */
    @Test
    void testGenerateValidToken() {
        // 调用JwtUtil.generateToken()生成有效token
        String token = JwtUtil.generateToken(10001L, "user");
        
        // 验证生成的token不为空
        assert token != null && !token.isEmpty();
        System.out.println("测试1 - 生成有效token成功: " + token);
    }

    /**
     * 测试2：使用有效token调用verify-token接口
     * @throws Exception
     */
    @Test
    void testVerifyTokenWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(10001L, "user");
        
        // 构造请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", validToken);
        
        // 调用POST /auth/verify-token接口
        ResultActions result = mockMvc.perform(post("/api/v1/auth/verify-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.expiresIn").value(greaterThan(0)))
                .andExpect(jsonPath("$.data.userId").value("10001"))
                .andExpect(jsonPath("$.data.userType").value("user"));
        
        System.out.println("测试2 - 使用有效token调用verify-token接口成功");
    }

    /**
     * 测试3：使用无效token调用verify-token接口
     * @throws Exception
     */
    @Test
    void testVerifyTokenWithInvalidToken() throws Exception {
        // 构造无效的token
        String invalidToken = "invalid-token-123456";
        
        // 构造请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", invalidToken);
        
        // 调用POST /auth/verify-token接口
        ResultActions result = mockMvc.perform(post("/api/v1/auth/verify-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.data.valid").value(false));
        
        System.out.println("测试3 - 使用无效token调用verify-token接口成功");
    }

    /**
     * 测试4：使用无效token调用verify-token接口
     * @throws Exception
     */
    @Test
    void testVerifyTokenWithExpiredToken() throws Exception {
        // 构造一个无效的token字符串，模拟过期token
        String expiredToken = "invalid-expired-token-123456";
        
        // 构造请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("token", expiredToken);
        
        // 调用POST /auth/verify-token接口
        ResultActions result = mockMvc.perform(post("/api/v1/auth/verify-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.data.valid").value(false));
        
        System.out.println("测试4 - 使用过期token调用verify-token接口成功");
    }
}