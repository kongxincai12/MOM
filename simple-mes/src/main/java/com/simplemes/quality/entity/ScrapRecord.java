package com.simplemes.quality.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 报废记录。
 */
@Data
@TableName("SMES_SCRAP_RECORD")
public class ScrapRecord {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String orderNo;
    private String productCode;
    /** 报废数量 */
    private BigDecimal qty;
    /** 缺陷代码 */
    private String defectCode;
    private String reason;
    private String operator;
    private Date scrapTime;
    private Date createTime;
}
