package com.simplemes.workorder.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.simplemes.common.Result;
import com.simplemes.workorder.entity.WorkOrder;
import com.simplemes.workorder.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 生产工单接口。
 */
@Tag(name = "生产工单", description = "工单 CRUD 与状态流转")
@RestController
@RequestMapping("/api/work-order")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    @PostMapping
    public Result<WorkOrder> create(@RequestBody WorkOrder workOrder) {
        return Result.success(workOrderService.create(workOrder));
    }

    @PutMapping
    public Result<WorkOrder> update(@RequestBody WorkOrder workOrder) {
        return Result.success(workOrderService.update(workOrder));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        workOrderService.delete(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<WorkOrder> get(@PathVariable String id) {
        return Result.success(workOrderService.get(id));
    }

    @GetMapping("/page")
    public Result<IPage<WorkOrder>> page(@RequestParam(defaultValue = "1") long current,
                                         @RequestParam(defaultValue = "10") long size,
                                         @RequestParam(required = false) String status,
                                         @RequestParam(required = false) String keyword) {
        return Result.success(workOrderService.page(current, size, status, keyword));
    }

    @PostMapping("/{id}/release")
    public Result<Void> release(@PathVariable String id) {
        workOrderService.release(id);
        return Result.success();
    }

    @PostMapping("/{id}/start")
    public Result<Void> start(@PathVariable String id) {
        workOrderService.start(id);
        return Result.success();
    }

    @PostMapping("/{id}/finish")
    public Result<Void> finish(@PathVariable String id) {
        workOrderService.finish(id);
        return Result.success();
    }

    @PostMapping("/{id}/close")
    public Result<Void> close(@PathVariable String id) {
        workOrderService.close(id);
        return Result.success();
    }
}
