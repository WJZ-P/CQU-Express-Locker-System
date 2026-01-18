package com.cqu.express.controller;

import com.cqu.express.common.Result;
import com.cqu.express.entity.User;
import com.cqu.express.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone) {
        
        // Simulating search by creating query or just findAll for simplicity in this generated code if params are empty
        Page<User> pageResult = userRepository.findAll(PageRequest.of(page - 1, pageSize));
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", pageResult.getContent());
        data.put("total", pageResult.getTotalElements());
        return Result.success(data);
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userRepository.deleteById(id);
        return Result.success();
    }
    
    @PutMapping("/{id}/status")
    public Result<Void> status(@PathVariable Long id, @RequestBody Map<String, String> body) {
        User u = userRepository.findById(id).orElse(null);
        if(u!=null) {
            u.setStatus(body.get("status"));
            userRepository.save(u);
        }
        return Result.success();
    }
}
