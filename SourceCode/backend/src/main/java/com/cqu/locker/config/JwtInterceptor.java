package com.cqu.locker.config;

import com.cqu.locker.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 跨域预检请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        // 获取请求路径
        String path = request.getServletPath();
        
        // 不需要认证的路径直接放行
        if (path.startsWith("/api/v1/auth/")) {
            return true;
        }
        
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            return false;
        }
        
        try {
            // 验证token
            if (!JwtUtil.verify(token)) {
                response.setStatus(401);
                return false;
            }
            
            // 将用户ID和类型存入request属性
            Long userId = JwtUtil.getUserId(token);
            String userType = JwtUtil.getUserType(token);
            request.setAttribute("userId", userId);
            request.setAttribute("userType", userType);
            
            // 管理端接口需要管理员权限
            if (path.startsWith("/api/v1/admin/")) {
                if (!"admin".equals(userType)) {
                    log.warn("非管理员用户尝试访问管理端接口: userId={}, userType={}, path={}", 
                            userId, userType, path);
                    response.setStatus(403); // Forbidden
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }
}
