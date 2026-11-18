package com.simplemes.material.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.simplemes.common.Result;
import com.simplemes.material.dto.StockOpRequest;
import com.simplemes.material.entity.Material;
import com.simplemes.material.entity.MaterialStock;
import com.simplemes.material.entity.StockRecord;
import com.simplemes.material.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 物料主数据 + 库存接口。
 */
@Tag(name = "物料库存", description = "物料主数据、库存与出入库流水")
@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    // ---- 物料主数据 ----
    @PostMapping
    public Result<Material> create(@RequestBody Material material) {
        return Result.success(materialService.createMaterial(material));
    }

    @PutMapping
    public Result<Material> update(@RequestBody Material material) {
        return Result.success(materialService.updateMaterial(material));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        materialService.deleteMaterial(id);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<IPage<Material>> page(@RequestParam(defaultValue = "1") long current,
                                        @RequestParam(defaultValue = "10") long size,
                                        @RequestParam(required = false) String keyword) {
        return Result.success(materialService.pageMaterial(current, size, keyword));
    }

    // ---- 库存 ----
    @GetMapping("/stock/page")
    public Result<IPage<MaterialStock>> stockPage(@RequestParam(defaultValue = "1") long current,
                                                  @RequestParam(defaultValue = "10") long size,
                                                  @RequestParam(required = false) String materialCode,
                                                  @RequestParam(required = false) String warehouse) {
        return Result.success(materialService.pageStock(current, size, materialCode, warehouse));
    }

    /** 入库 / 出库 / 领料 / 退料 */
    @PostMapping("/stock/operate")
    public Result<StockRecord> stockOperate(@RequestBody StockOpRequest request) {
        return Result.success(materialService.stockOperate(request));
    }

    @GetMapping("/stock/record/page")
    public Result<IPage<StockRecord>> recordPage(@RequestParam(defaultValue = "1") long current,
                                                 @RequestParam(defaultValue = "10") long size,
                                                 @RequestParam(required = false) String materialCode) {
        return Result.success(materialService.pageStockRecords(current, size, materialCode));
    }
}
