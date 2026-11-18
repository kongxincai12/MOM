package com.simplemes.quality.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.simplemes.quality.dto.QualityReportDto;
import com.simplemes.quality.entity.ReworkRecord;
import com.simplemes.quality.entity.ScrapRecord;

import java.util.List;

/**
 * 质量服务：报废、返工、质量报表。
 */
public interface QualityService {

    ScrapRecord createScrap(ScrapRecord record);

    IPage<ScrapRecord> pageScrap(long current, long size, String orderNo);

    ReworkRecord createRework(ReworkRecord record);

    ReworkRecord updateReworkStatus(String id, String status);

    IPage<ReworkRecord> pageRework(long current, long size, String orderNo);

    /** 质量报表（按工单聚合，含良率/报废率） */
    List<QualityReportDto> qualityReport(String orderNo);
}
