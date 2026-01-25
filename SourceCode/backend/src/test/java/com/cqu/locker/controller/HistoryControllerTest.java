package com.cqu.locker.controller;

import com.cqu.locker.utils.JwtUtil;
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
 * HistoryController测试类
 * 测试历史记录模块接口功能
 */
@SpringBootTest
@AutoConfigureMockMvc
class HistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 测试1：使用有效token获取历史记录
     * @throws Exception
     */
    @Test
    void testGetHistoryWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(2L, "user");
        
        // 调用GET /api/v1/history接口
        ResultActions result = mockMvc.perform(get("/api/v1/history")
                .header("Authorization", "Bearer " + validToken)
                .param("type", "all")
                .param("page", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.total").isNumber())
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.list").isArray());
        
        System.out.println("测试1 - 使用有效token获取历史记录成功");
    }

    /**
     * 测试2：使用有效token获取特定类型的历史记录（已取件）
     * @throws Exception
     */
    @Test
    void testGetHistoryWithTypeWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(2L, "user");
        
        // 调用GET /api/v1/history接口，指定type为"picked"
        ResultActions result = mockMvc.perform(get("/api/v1/history")
                .header("Authorization", "Bearer " + validToken)
                .param("type", "picked")
                .param("page", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.list").isArray());
        
        System.out.println("测试2 - 使用有效token获取特定类型的历史记录成功");
    }

    /**
     * 测试3：使用无效token获取历史记录
     * @throws Exception
     */
    @Test
    void testGetHistoryWithInvalidToken() throws Exception {
        // 构造无效token
        String invalidToken = "invalid-token-123456";
        
        // 调用GET /api/v1/history接口
        ResultActions result = mockMvc.perform(get("/api/v1/history")
                .header("Authorization", "Bearer " + invalidToken)
                .param("type", "all")
                .param("page", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isUnauthorized());
        
        System.out.println("测试3 - 使用无效token获取历史记录返回401，符合预期");
    }

    /**
     * 测试4：使用有效token获取指定页码的历史记录
     * @throws Exception
     */
    @Test
    void testGetHistoryWithPageWithValidToken() throws Exception {
        // 生成有效token
        String validToken = JwtUtil.generateToken(2L, "user");
        
        // 调用GET /api/v1/history接口，指定page为2
        ResultActions result = mockMvc.perform(get("/api/v1/history")
                .header("Authorization", "Bearer " + validToken)
                .param("type", "all")
                .param("page", "2")
                .param("pageSize", "5")
                .contentType(MediaType.APPLICATION_JSON));
        
        // 验证响应
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.page").value(2))
                .andExpect(jsonPath("$.data.pageSize").value(5))
                .andExpect(jsonPath("$.data.list").isArray());
        
        System.out.println("测试4 - 使用有效token获取指定页码的历史记录成功");
    }
}
