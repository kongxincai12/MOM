package com.simplemes.production.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产报工记录（人工 / 设备）。
 */
@Data
@TableName("SMES_PRODUCTION_REPORT")
public class ProductionReport {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 工单号 */
    private String orderNo;
    /** 报工类型：MANUAL/EQUIPMENT */
    private String reportType;
    /** 报工工号 */
    private String employeeId;
    /** 设备ID（设备报工时填写） */
    private String equipmentId;
    /** 合格数量 */
    private BigDecimal qualifiedQty;
    /** 工时 */
    private BigDecimal workHours;
    /** 班次：DAY/NIGHT */
    private String shift;
    private Date reportTime;
    private Date createTime;
}
