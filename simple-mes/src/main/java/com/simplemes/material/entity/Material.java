package com.simplemes.material.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 物料主数据。
 */
@Data
@TableName("SMES_MATERIAL")
public class Material {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String materialCode;
    private String materialName;
    private String spec;
    private String unit;
    /** RAW原材料/SEMI半成品/FINISH成品 */
    private String category;
    private Date createTime;
    private Date updateTime;
}
