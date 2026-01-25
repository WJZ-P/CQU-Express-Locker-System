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

/**
 * LockerController测试类
 * 测试快递柜模块接口功能
 */
@SpringBootTest
@AutoConfigureMockMvc
class LockerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试1：使用有效token获取快递柜列表
     * @throws Exception
     */
    @Test
    void testGetLockerListWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(10001L, "user");
        
        // 调用GET /locker/list接口
        ResultActions result = mockMvc.perform(get("/api/v1/locker/list")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data").isArray());
        
        System.out.println("测试1 - 使用有效token获取快递柜列表成功");
    }

    /**
     * 测试2：使用无效token获取快递柜列表
     * @throws Exception
     */
    @Test
    void testGetLockerListWithInvalidToken() throws Exception {
        // 构造无效token
        String invalidToken = "invalid-token-123456";
        
        // 调用GET /locker/list接口
        ResultActions result = mockMvc.perform(get("/api/v1/locker/list")
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isUnauthorized());
        
        System.out.println("测试2 - 使用无效token获取快递柜列表返回401，符合预期");
    }

    /**
     * 测试3：使用有效token获取快递柜详情
     * @throws Exception
     */
    @Test
    void testGetLockerDetailWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(10001L, "user");
        
        // 调用GET /locker/{lockerId}接口
        ResultActions result = mockMvc.perform(get("/api/v1/locker/1")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(1));
        
        System.out.println("测试3 - 使用有效token获取快递柜详情成功");
    }

    /**
     * 测试4：使用无效token获取快递柜详情
     * @throws Exception
     */
    @Test
    void testGetLockerDetailWithInvalidToken() throws Exception {
        // 构造无效token
        String invalidToken = "invalid-token-123456";
        
        // 调用GET /locker/{lockerId}接口
        ResultActions result = mockMvc.perform(get("/api/v1/locker/1")
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isUnauthorized());
        
        System.out.println("测试4 - 使用无效token获取快递柜详情返回401，符合预期");
    }
}
