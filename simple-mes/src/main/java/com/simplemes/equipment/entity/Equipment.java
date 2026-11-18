package com.simplemes.equipment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 设备台账。
 */
@Data
@TableName("SMES_EQUIPMENT")
public class Equipment {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /** 设备编号 */
    private String eqpCode;
    private String eqpName;
    /** 设备类型 */
    private String eqpTypeId;
    /** 位置/线体 */
    private String location;
    /** RUNNING运行/IDLE空闲/MAINTENANCE维修 */
    private String status;
    private Date createTime;
    private Date updateTime;
}
