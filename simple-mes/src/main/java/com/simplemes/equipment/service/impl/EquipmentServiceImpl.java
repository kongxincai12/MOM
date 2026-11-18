package com.simplemes.equipment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplemes.common.BusinessException;
import com.simplemes.equipment.entity.Equipment;
import com.simplemes.equipment.mapper.EquipmentMapper;
import com.simplemes.equipment.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    private static final List<String> ALLOWED_STATUS = Arrays.asList("RUNNING", "IDLE", "MAINTENANCE");

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Override
    @Transactional
    public Equipment create(Equipment equipment) {
        if (!StringUtils.hasText(equipment.getEqpCode())) {
            throw new BusinessException("设备编号不能为空");
        }
        if (equipmentMapper.selectByCode(equipment.getEqpCode()) != null) {
            throw new BusinessException("设备编号已存在: " + equipment.getEqpCode());
        }
        equipment.setId(null);
        if (!StringUtils.hasText(equipment.getStatus())) {
            equipment.setStatus("IDLE");
        }
        equipment.setCreateTime(new Date());
        equipment.setUpdateTime(new Date());
        equipmentMapper.insert(equipment);
        return equipment;
    }

    @Override
    @Transactional
    public Equipment update(Equipment equipment) {
        Equipment db = require(equipment.getId());
        equipment.setCreateTime(db.getCreateTime());
        if (!StringUtils.hasText(equipment.getStatus())) {
            equipment.setStatus(db.getStatus());
        }
        equipment.setUpdateTime(new Date());
        equipmentMapper.updateById(equipment);
        return equipment;
    }

    @Override
    @Transactional
    public void delete(String id) {
        require(id);
        equipmentMapper.deleteById(id);
    }

    @Override
    public Equipment get(String id) {
        return require(id);
    }

    @Override
    public IPage<Equipment> page(long current, long size, String status, String keyword) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(Equipment::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Equipment::getEqpCode, keyword)
                    .or().like(Equipment::getEqpName, keyword));
        }
        wrapper.orderByDesc(Equipment::getCreateTime);
        return equipmentMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    @Transactional
    public void updateStatus(String id, String status) {
        Equipment db = require(id);
        if (status == null || !ALLOWED_STATUS.contains(status.toUpperCase())) {
            throw new BusinessException("设备状态非法，仅支持 RUNNING/IDLE/MAINTENANCE");
        }
        db.setStatus(status.toUpperCase());
        db.setUpdateTime(new Date());
        equipmentMapper.updateById(db);
    }

    private Equipment require(String id) {
        Equipment db = equipmentMapper.selectById(id);
        if (db == null) {
            throw new BusinessException("设备不存在: " + id);
        }
        return db;
    }
}
