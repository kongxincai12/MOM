package com.simplemes.material.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 库存操作请求（入库 / 出库 / 领料 / 退料）。
 */
@Data
public class StockOpRequest {

    private String materialCode;
    private String warehouse;
    /** 操作数量，正数 */
    private BigDecimal qty;
    /** IN入库/OUT出库/ISSUE领料/RETURN退料 */
    private String bizType;
    /** 关联工单（领料/退料时使用） */
    private String orderNo;
    private String operator;
}
