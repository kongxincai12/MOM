package com.simplemes.production.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.simplemes.production.entity.ProductionReport;

/**
 * 生产报工服务。
 */
public interface ProductionReportService {

    /** 提交报工，并累加工单完成数量 */
    ProductionReport report(ProductionReport report);

    IPage<ProductionReport> page(long current, long size, String orderNo);
}
