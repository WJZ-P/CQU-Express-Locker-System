package com.cqu.express.controller;

import com.cqu.express.common.Result;
import com.cqu.express.entity.User;
import com.cqu.express.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;

    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        // Mock admin if not exists
        if ("admin".equals(username) && "123456".equals(password)) {
            User admin = userRepository.findByUsername("admin");
            if (admin == null) {
                admin = new User();
                admin.setUsername("admin");
                admin.setPassword("123456");
                admin.setName("管理员");
                admin.setRole("admin");
                userRepository.save(admin);
            }
        }

        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(KEY)
                .compact();
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userInfo", user);
            return Result.success(data);
        }
        return Result.error(401, "用户名或密码错误");
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("Logged out");
    }

    @GetMapping("/userinfo")
    public Result<User> getUserInfo(@RequestHeader(value="Authorization", required=false) String token) {
        // In reality, parse token and get user
        // For simple demo, return a mock or parse it if you want
        return Result.success(userRepository.findByUsername("admin")); 
    }
}
