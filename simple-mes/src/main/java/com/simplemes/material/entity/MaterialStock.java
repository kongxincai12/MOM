package com.simplemes.material.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 物料库存（按物料 + 仓库维度）。
 */
@Data
@TableName("SMES_MATERIAL_STOCK")
public class MaterialStock {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String materialCode;
    private String warehouse;
    private BigDecimal qty;
    private String unit;
    private Date updateTime;
}
