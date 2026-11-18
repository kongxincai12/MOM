package com.simplemes.equipment.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.simplemes.common.Result;
import com.simplemes.equipment.entity.Equipment;
import com.simplemes.equipment.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 设备管理接口。
 */
@Tag(name = "设备管理", description = "设备台账与状态维护")
@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @PostMapping
    public Result<Equipment> create(@RequestBody Equipment equipment) {
        return Result.success(equipmentService.create(equipment));
    }

    @PutMapping
    public Result<Equipment> update(@RequestBody Equipment equipment) {
        return Result.success(equipmentService.update(equipment));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        equipmentService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<Equipment> get(@PathVariable String id) {
        return Result.success(equipmentService.get(id));
    }

    @GetMapping("/page")
    public Result<IPage<Equipment>> page(@RequestParam(defaultValue = "1") long current,
                                         @RequestParam(defaultValue = "10") long size,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(required = false) String keyword) {
        return Result.success(equipmentService.page(current, size, status, keyword));
    }

    @PostMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable String id, @RequestParam String status) {
        equipmentService.updateStatus(id, status);
        return Result.success();
    }
}
