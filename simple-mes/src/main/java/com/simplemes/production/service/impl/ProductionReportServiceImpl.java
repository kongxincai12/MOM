package com.simplemes.production.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplemes.common.BusinessException;
import com.simplemes.production.entity.ProductionReport;
import com.simplemes.production.mapper.ProductionReportMapper;
import com.simplemes.production.service.ProductionReportService;
import com.simplemes.workorder.entity.WorkOrder;
import com.simplemes.workorder.mapper.WorkOrderMapper;
import com.simplemes.workorder.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class ProductionReportServiceImpl implements ProductionReportService {

    @Autowired
    private ProductionReportMapper reportMapper;
    @Autowired
    private WorkOrderMapper workOrderMapper;
    @Autowired
    private WorkOrderService workOrderService;

    @Override
    @Transactional
    public ProductionReport report(ProductionReport report) {
        if (!StringUtils.hasText(report.getOrderNo())) {
            throw new BusinessException("工单号不能为空");
        }
        if (report.getQualifiedQty() == null || report.getQualifiedQty().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("合格数量不能为负");
        }
        WorkOrder order = workOrderMapper.selectByOrderNo(report.getOrderNo());
        if (order == null) {
            throw new BusinessException("工单不存在: " + report.getOrderNo());
        }
        // 演示模式：不校验工单状态，任意状态（CREATED/RELEASED/STARTED/FINISHED/CLOSED）均可报工，仅要求工单存在。

        if (!StringUtils.hasText(report.getReportType())) {
            report.setReportType("MANUAL");
        }
        report.setId(null);
        report.setCreateTime(new Date());
        if (report.getReportTime() == null) {
            report.setReportTime(new Date());
        }
        if (report.getWorkHours() == null) {
            report.setWorkHours(BigDecimal.ZERO);
        }
        reportMapper.insert(report);

        // 累加工单完成数量
        workOrderService.increaseFinishQty(report.getOrderNo(), report.getQualifiedQty());
        return report;
    }

    @Override
    public IPage<ProductionReport> page(long current, long size, String orderNo) {
        LambdaQueryWrapper<ProductionReport> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(orderNo)) {
            wrapper.eq(ProductionReport::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(ProductionReport::getCreateTime);
        return reportMapper.selectPage(new Page<>(current, size), wrapper);
    }
}
