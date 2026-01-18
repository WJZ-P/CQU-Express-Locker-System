package com.cqu.express.controller;

import com.cqu.express.common.Result;
import com.cqu.express.entity.Courier;
import com.cqu.express.repository.CourierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/courier")
@CrossOrigin
public class CourierController {

    @Autowired
    private CourierRepository courierRepository;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<Courier> pageResult = courierRepository.findAll(PageRequest.of(page - 1, pageSize));
        Map<String, Object> data = new HashMap<>();
        data.put("list", pageResult.getContent());
        data.put("total", pageResult.getTotalElements());
        return Result.success(data);
    }
    
    @PostMapping
    public Result<Courier> add(@RequestBody Courier courier) {
        return Result.success(courierRepository.save(courier));
    }
    
    @PutMapping("/{id}")
    public Result<Courier> update(@PathVariable Long id, @RequestBody Courier courier) {
        courier.setId(id);
        return Result.success(courierRepository.save(courier));
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        courierRepository.deleteById(id);
        return Result.success();
    }
}
