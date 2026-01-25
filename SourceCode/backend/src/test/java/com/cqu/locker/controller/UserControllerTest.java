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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.Map;

/**
 * UserController测试类
 * 测试用户模块接口功能
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试1：使用有效token获取用户信息
     * @throws Exception
     */
    @Test
    void testGetUserProfileWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(2L, "user");
        
        // 调用GET /user/profile接口
        ResultActions result = mockMvc.perform(get("/api/v1/user/profile")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.userId").value(2));
        
        System.out.println("测试1 - 使用有效token获取用户信息成功");
    }

    /**
     * 测试2：使用无效token获取用户信息
     * @throws Exception
     */
    @Test
    void testGetUserProfileWithInvalidToken() throws Exception {
        // 构造无效token
        String invalidToken = "invalid-token-123456";
        
        // 调用GET /user/profile接口
        ResultActions result = mockMvc.perform(get("/api/v1/user/profile")
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isUnauthorized());
        
        System.out.println("测试2 - 使用无效token获取用户信息返回401，符合预期");
    }

    /**
     * 测试3：使用有效token更新用户信息
     * @throws Exception
     */
    @Test
    void testUpdateUserProfileWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(2L, "user");
        
        // 构造请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("nickname", "测试用户");
        
        // 调用PUT /user/profile接口
        ResultActions result = mockMvc.perform(put("/api/v1/user/profile")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
        
        System.out.println("测试3 - 使用有效token更新用户信息成功");
    }
}
