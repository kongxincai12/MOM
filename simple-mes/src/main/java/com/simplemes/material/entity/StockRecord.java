package com.simplemes.material.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 出入库流水。
 */
@Data
@TableName("SMES_STOCK_RECORD")
public class StockRecord {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String materialCode;
    private String warehouse;
    /** IN入库/OUT出库/ISSUE领料/RETURN退料 */
    private String bizType;
    private BigDecimal qty;
    private String orderNo;
    private String operator;
    private Date createTime;
}
