package com.simplemes.quality.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 质量报表行（按工单聚合）。
 */
@Data
public class QualityReportDto {

    private String orderNo;
    private String productName;
    /** 计划数量 */
    private BigDecimal planQty;
    /** 合格数量（来自生产报工合计） */
    private BigDecimal qualifiedQty;
    /** 报废数量合计 */
    private BigDecimal scrapQty;
    /** 返工数量合计 */
    private BigDecimal reworkQty;
    /** 良率 = 合格 / (合格 + 报废) */
    private BigDecimal passRate;
    /** 报废率 = 报废 / (合格 + 报废) */
    private BigDecimal scrapRate;
}
