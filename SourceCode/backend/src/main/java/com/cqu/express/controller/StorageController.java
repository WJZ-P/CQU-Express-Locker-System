package com.cqu.express.controller;

import com.cqu.express.common.Result;
import com.cqu.express.entity.StorageRecord;
import com.cqu.express.repository.StorageRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/storage")
@CrossOrigin
public class StorageController {

    @Autowired
    private StorageRecordRepository storageRecordRepository;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String depositor,
            @RequestParam(required = false) String status) {
        
        Page<StorageRecord> pageResult = storageRecordRepository.findAll(PageRequest.of(page - 1, pageSize));
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", pageResult.getContent());
        data.put("total", pageResult.getTotalElements());
        return Result.success(data);
    }
    
    @PostMapping
    public Result<StorageRecord> create(@RequestBody StorageRecord record) {
        record.setStatus("寄存中");
        return Result.success(storageRecordRepository.save(record));
    }
    
    @PutMapping("/{id}/pickup")
    public Result<Void> pickup(@PathVariable Long id) {
        StorageRecord record = storageRecordRepository.findById(id).orElse(null);
        if(record != null && "寄存中".equals(record.getStatus())) {
            record.setStatus("已取出");
            record.setPickupTime(LocalDateTime.now());
            storageRecordRepository.save(record);
            return Result.success();
        }
        return Result.error("记录不存在或状态不正确");
    }
    
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        storageRecordRepository.deleteById(id);
        return Result.success();
    }
}
