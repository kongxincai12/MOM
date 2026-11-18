package com.simplemes.quality.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.simplemes.common.Result;
import com.simplemes.quality.dto.QualityReportDto;
import com.simplemes.quality.entity.ReworkRecord;
import com.simplemes.quality.entity.ScrapRecord;
import com.simplemes.quality.service.QualityService;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 质量管理接口：报废 / 返工 / 质量报表。
 */
@Tag(name = "质量管理", description = "报废 / 返工 / 质量报表")
@RestController
@RequestMapping("/api/quality")
public class QualityController {

    @Autowired
    private QualityService qualityService;

    // ---- 报废 ----
    @PostMapping("/scrap")
    public Result<ScrapRecord> createScrap(@RequestBody ScrapRecord record) {
        return Result.success(qualityService.createScrap(record));
    }

    @GetMapping("/scrap/page")
    public Result<IPage<ScrapRecord>> pageScrap(@RequestParam(defaultValue = "1") long current,
                                                @RequestParam(defaultValue = "10") long size,
                                                @RequestParam(required = false) String orderNo) {
        return Result.success(qualityService.pageScrap(current, size, orderNo));
    }

    // ---- 返工 ----
    @PostMapping("/rework")
    public Result<ReworkRecord> createRework(@RequestBody ReworkRecord record) {
        return Result.success(qualityService.createRework(record));
    }

    @PostMapping("/rework/{id}/status")
    public Result<ReworkRecord> updateReworkStatus(@PathVariable String id, @RequestParam String status) {
        return Result.success(qualityService.updateReworkStatus(id, status));
    }

    @GetMapping("/rework/page")
    public Result<IPage<ReworkRecord>> pageRework(@RequestParam(defaultValue = "1") long current,
                                                  @RequestParam(defaultValue = "10") long size,
                                                  @RequestParam(required = false) String orderNo) {
        return Result.success(qualityService.pageRework(current, size, orderNo));
    }

    // ---- 质量报表 ----
    @GetMapping("/report")
    public Result<List<QualityReportDto>> report(@RequestParam(required = false) String orderNo) {
        return Result.success(qualityService.qualityReport(orderNo));
    }
}
