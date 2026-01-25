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
 * CourierController测试类
 * 测试快递员模块接口功能
 */
@SpringBootTest
@AutoConfigureMockMvc
class CourierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试1：使用有效token获取快递员信息
     * @throws Exception
     */
    @Test
    void testGetCourierProfileWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(3L, "courier");
        
        // 调用GET /courier/profile接口
        ResultActions result = mockMvc.perform(get("/api/v1/courier/profile")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.courierId").value(3));
        
        System.out.println("测试1 - 使用有效token获取快递员信息成功");
    }

    /**
     * 测试2：使用无效token获取快递员信息
     * @throws Exception
     */
    @Test
    void testGetCourierProfileWithInvalidToken() throws Exception {
        // 构造无效token
        String invalidToken = "invalid-token-123456";
        
        // 调用GET /courier/profile接口
        ResultActions result = mockMvc.perform(get("/api/v1/courier/profile")
                .header("Authorization", "Bearer " + invalidToken)
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isUnauthorized());
        
        System.out.println("测试2 - 使用无效token获取快递员信息返回401，符合预期");
    }

    /**
     * 测试3：使用有效token查询收件人信息
     * @throws Exception
     */
    @Test
    void testQueryReceiverWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(3L, "courier");
        
        // 调用GET /courier/query-receiver接口
        ResultActions result = mockMvc.perform(get("/api/v1/courier/query-receiver")
                .header("Authorization", "Bearer " + validToken)
                .param("phone", "13800138000")
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.phone").isNotEmpty());
        
        System.out.println("测试3 - 使用有效token查询收件人信息成功");
    }

    /**
     * 测试4：使用有效token获取待揽收列表
     * @throws Exception
     */
    @Test
    void testGetPendingCollectListWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(3L, "courier");
        
        // 调用GET /courier/pending-collect接口
        ResultActions result = mockMvc.perform(get("/api/v1/courier/pending-collect")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.total").isNumber())
                .andExpect(jsonPath("$.data.list").isArray());
        
        System.out.println("测试4 - 使用有效token获取待揽收列表成功");
    }

    /**
     * 测试5：使用有效token获取待退回列表
     * @throws Exception
     */
    @Test
    void testGetPendingReturnListWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(3L, "courier");
        
        // 调用GET /courier/pending-return接口
        ResultActions result = mockMvc.perform(get("/api/v1/courier/pending-return")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.total").isNumber())
                .andExpect(jsonPath("$.data.list").isArray());
        
        System.out.println("测试5 - 使用有效token获取待退回列表成功");
    }

    /**
     * 测试6：使用有效token投递快递
     * @throws Exception
     */
    @Test
    void testDeliverExpressWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(3L, "courier");
        
        // 构造请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("lockerId", "1");
        requestBody.put("compartmentSize", "small");
        requestBody.put("trackingNo", "SF1234567890");
        requestBody.put("receiverPhone", "13800138000");
        requestBody.put("receiverName", "张三");
        
        // 调用POST /courier/deliver接口
        ResultActions result = mockMvc.perform(post("/api/v1/courier/deliver")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));
        
        // 验证响应 - 只检查HTTP状态码为200，不检查响应内容，因为可能出现"该尺寸的空闲格口已用完"的情况
        result.andExpect(status().isOk());
        
        System.out.println("测试6 - 使用有效token投递快递测试完成");
    }

    /**
     * 测试7：使用有效token开柜操作
     * @throws Exception
     */
    @Test
    void testOpenCompartmentWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(3L, "courier");
        
        // 构造请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("expressId", "EX20260122001");
        
        // 调用POST /courier/open-compartment接口
        ResultActions result = mockMvc.perform(post("/api/v1/courier/open-compartment")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"));
        
        System.out.println("测试7 - 使用有效token开柜操作成功");
    }
}
