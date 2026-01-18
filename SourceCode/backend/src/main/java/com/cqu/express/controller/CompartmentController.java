package com.cqu.express.controller;

import com.cqu.express.common.Result;
import com.cqu.express.entity.Compartment;
import com.cqu.express.repository.CompartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compartment")
@CrossOrigin
public class CompartmentController {

    @Autowired
    private CompartmentRepository compartmentRepository;

    @GetMapping("/list")
    public Result<List<Compartment>> list(@RequestParam String lockerId) {
        return Result.success(compartmentRepository.findByLockerId(lockerId));
    }
    
    @PutMapping("/{id}")
    public Result<Compartment> update(@PathVariable String id, @RequestBody Compartment compartment) {
        Compartment c = compartmentRepository.findById(id).orElse(null);
        if (c != null) {
            c.setStatus(compartment.getStatus());
            // simulate sensors update
            c.setTemperature(20 + Math.random() * 10); 
            c.setPressure(compartment.getStatus().equals("占用") ? 5.0 + Math.random() : 0.0);
            return Result.success(compartmentRepository.save(c));
        }
        return Result.error("仓门不存在");
    }

    @PostMapping("/{id}/open")
    public Result<String> open(@PathVariable String id) {
        return Result.success("仓门已远程开启");
    }
}
