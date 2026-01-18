package com.cqu.express.controller;

import com.cqu.express.common.Result;
import com.cqu.express.entity.ExpressOrder;
import com.cqu.express.repository.ExpressOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/express")
@CrossOrigin
public class ExpressController {

    @Autowired
    private ExpressOrderRepository expressOrderRepository;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String trackingNo,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String status) {
        
        Page<ExpressOrder> pageResult = expressOrderRepository.findByCondition(trackingNo, phone, "all".equals(status) ? null : status, PageRequest.of(page - 1, pageSize));
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", pageResult.getContent());
        data.put("total", pageResult.getTotalElements());
        return Result.success(data);
    }
}
