package com.simplemes.production.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.simplemes.common.Result;
import com.simplemes.production.entity.ProductionReport;
import com.simplemes.production.service.ProductionReportService;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 生产报工接口。
 */
@Tag(name = "生产报工", description = "人工/设备报工，累加工单完成数量")
@RestController
@RequestMapping("/api/production-report")
public class ProductionReportController {

    @Autowired
    private ProductionReportService productionReportService;

    @PostMapping
    public Result<ProductionReport> report(@RequestBody ProductionReport report) {
        return Result.success(productionReportService.report(report));
    }

    @GetMapping("/page")
    public Result<IPage<ProductionReport>> page(@RequestParam(defaultValue = "1") long current,
                                                @RequestParam(defaultValue = "10") long size,
                                                @RequestParam(required = false) String orderNo) {
        return Result.success(productionReportService.page(current, size, orderNo));
    }
}
