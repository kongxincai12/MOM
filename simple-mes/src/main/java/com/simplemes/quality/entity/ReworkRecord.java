package com.simplemes.quality.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 返工记录。
 */
@Data
@TableName("SMES_REWORK_RECORD")
public class ReworkRecord {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String orderNo;
    private String productCode;
    /** 返工数量 */
    private BigDecimal qty;
    private String reason;
    /** PENDING待返工/DOING返工中/DONE已完成 */
    private String status;
    private String operator;
    private Date reworkDate;
    private Date createTime;
    private Date updateTime;
}
