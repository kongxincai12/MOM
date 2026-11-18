package com.simplemes.workorder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产工单。
 */
@Data
@TableName("SMES_WORK_ORDER")
public class WorkOrder {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 工单号（业务唯一） */
    private String orderNo;
    /** 产品物料编码 */
    private String productCode;
    /** 产品名称 */
    private String productName;
    /** 计划数量 */
    private BigDecimal planQty;
    /** 已完成（合格）数量 */
    private BigDecimal finishQty;
    /** 产线 */
    private String lineCode;
    /** 状态：CREATED/RELEASED/STARTED/FINISHED/CLOSED */
    private String status;
    private Date startDate;
    private Date endDate;
    private String remark;
    private Date createTime;
    private Date updateTime;
}
