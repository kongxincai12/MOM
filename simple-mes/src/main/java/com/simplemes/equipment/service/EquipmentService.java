package com.simplemes.equipment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.simplemes.equipment.entity.Equipment;

/**
 * 设备管理服务。
 */
public interface EquipmentService {

    Equipment create(Equipment equipment);

    Equipment update(Equipment equipment);

    void delete(String id);

    Equipment get(String id);

    IPage<Equipment> page(long current, long size, String status, String keyword);

    /** 更新设备状态：RUNNING/IDLE/MAINTENANCE */
    void updateStatus(String id, String status);
}
