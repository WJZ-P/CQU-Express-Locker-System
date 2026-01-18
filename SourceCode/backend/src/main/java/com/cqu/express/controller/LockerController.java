package com.cqu.express.controller;

import com.cqu.express.common.Result;
import com.cqu.express.entity.Compartment;
import com.cqu.express.entity.Locker;
import com.cqu.express.repository.CompartmentRepository;
import com.cqu.express.repository.LockerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/locker")
@CrossOrigin
public class LockerController {

    @Autowired
    private LockerRepository lockerRepository;

    @Autowired
    private CompartmentRepository compartmentRepository;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String location) {
        
        Page<Locker> pageResult = lockerRepository.findByCondition(id, location, PageRequest.of(page - 1, pageSize));
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", pageResult.getContent());
        data.put("total", pageResult.getTotalElements());
        return Result.success(data);
    }

    @PostMapping
    public Result<Locker> add(@RequestBody Locker locker) {
        lockerRepository.save(locker);
        // Create compartments
        for (int i = 1; i <= locker.getCompartmentCount(); i++) {
            Compartment c = new Compartment();
            c.setId(locker.getId() + String.format("-C%02d", i));
            c.setLockerId(locker.getId());
            compartmentRepository.save(c);
        }
        return Result.success(locker);
    }

    @PutMapping("/{id}")
    public Result<Locker> update(@PathVariable String id, @RequestBody Locker locker) {
        Locker old = lockerRepository.findById(id).orElse(null);
        if (old != null) {
            old.setLocation(locker.getLocation());
            old.setRemark(locker.getRemark());
            lockerRepository.save(old);
        }
        return Result.success(old);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        lockerRepository.deleteById(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable String id, @RequestBody Map<String, String> body) {
        Locker locker = lockerRepository.findById(id).orElse(null);
        if (locker != null) {
            locker.setStatus(body.get("status"));
            lockerRepository.save(locker);
        }
        return Result.success();
    }
}
