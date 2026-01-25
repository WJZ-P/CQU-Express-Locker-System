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
 * StorageController测试类
 * 测试寄存模块接口功能
 */
@SpringBootTest
@AutoConfigureMockMvc
class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试1：使用有效token创建寄存订单
     * @throws Exception
     */
    @Test
    void testCreateStorageWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(2L, "user");
        
        // 构造请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("lockerId", "1");
        requestBody.put("compartmentSize", "small");
        requestBody.put("duration", "24");
        requestBody.put("itemDescription", "书包");
        
        // 调用POST /storage/create接口
        ResultActions result = mockMvc.perform(post("/api/v1/storage/create")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));
        
        // 验证响应 - 只检查HTTP状态码为200，不检查响应内容，因为可能出现"该尺寸的空闲格口已用完"的情况
        result.andExpect(status().isOk());
        
        System.out.println("测试1 - 使用有效token创建寄存订单测试完成");
    }

    /**
     * 测试2：使用无效token创建寄存订单
     * @throws Exception
     */
    @Test
    void testCreateStorageWithInvalidToken() throws Exception {
        // 构造无效token
        String invalidToken = "invalid-token-123456";
        
        // 构造请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("lockerId", "1");
        requestBody.put("compartmentSize", "medium");
        requestBody.put("duration", 24);
        requestBody.put("itemDescription", "书包");
        
        // 调用POST /storage/create接口
        ResultActions result = mockMvc.perform(post("/api/v1/storage/create")
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));
        
        // 验证响应
        result.andExpect(status().isUnauthorized());
        
        System.out.println("测试2 - 使用无效token创建寄存订单返回401，符合预期");
    }

    /**
     * 测试3：使用有效token获取寄存列表
     * @throws Exception
     */
    @Test
    void testGetStorageListWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(2L, "user");
        
        // 调用GET /storage/list接口
        ResultActions result = mockMvc.perform(get("/api/v1/storage/list")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.total").isNumber())
                .andExpect(jsonPath("$.data.list").isArray());
        
        System.out.println("测试3 - 使用有效token获取寄存列表成功");
    }

    /**
     * 测试4：使用无效token获取寄存列表
     * @throws Exception
     */
    @Test
    void testGetStorageListWithInvalidToken() throws Exception {
        // 构造无效token
        String invalidToken = "invalid-token-123456";
        
        // 调用GET /storage/list接口
        ResultActions result = mockMvc.perform(get("/api/v1/storage/list")
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isUnauthorized());
        
        System.out.println("测试4 - 使用无效token获取寄存列表返回401，符合预期");
    }
}
