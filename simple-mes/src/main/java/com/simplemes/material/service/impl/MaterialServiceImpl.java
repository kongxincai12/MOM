package com.simplemes.material.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplemes.common.BusinessException;
import com.simplemes.material.dto.StockOpRequest;
import com.simplemes.material.entity.Material;
import com.simplemes.material.entity.MaterialStock;
import com.simplemes.material.entity.StockRecord;
import com.simplemes.material.mapper.MaterialMapper;
import com.simplemes.material.mapper.MaterialStockMapper;
import com.simplemes.material.mapper.StockRecordMapper;
import com.simplemes.material.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class MaterialServiceImpl implements MaterialService {

    private static final String DEFAULT_WAREHOUSE = "WH01";

    @Autowired
    private MaterialMapper materialMapper;
    @Autowired
    private MaterialStockMapper stockMapper;
    @Autowired
    private StockRecordMapper stockRecordMapper;

    @Override
    @Transactional
    public Material createMaterial(Material material) {
        if (!StringUtils.hasText(material.getMaterialCode())) {
            throw new BusinessException("物料编码不能为空");
        }
        if (materialMapper.selectByCode(material.getMaterialCode()) != null) {
            throw new BusinessException("物料编码已存在: " + material.getMaterialCode());
        }
        material.setId(null);
        material.setCreateTime(new Date());
        material.setUpdateTime(new Date());
        materialMapper.insert(material);
        return material;
    }

    @Override
    @Transactional
    public Material updateMaterial(Material material) {
        Material db = materialMapper.selectById(material.getId());
        if (db == null) {
            throw new BusinessException("物料不存在");
        }
        material.setCreateTime(db.getCreateTime());
        material.setUpdateTime(new Date());
        materialMapper.updateById(material);
        return material;
    }

    @Override
    @Transactional
    public void deleteMaterial(String id) {
        materialMapper.deleteById(id);
    }

    @Override
    public IPage<Material> pageMaterial(long current, long size, String keyword) {
        LambdaQueryWrapper<Material> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Material::getMaterialCode, keyword)
                    .or().like(Material::getMaterialName, keyword));
        }
        wrapper.orderByDesc(Material::getCreateTime);
        return materialMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public IPage<MaterialStock> pageStock(long current, long size, String materialCode, String warehouse) {
        LambdaQueryWrapper<MaterialStock> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(materialCode)) {
            wrapper.eq(MaterialStock::getMaterialCode, materialCode);
        }
        if (StringUtils.hasText(warehouse)) {
            wrapper.eq(MaterialStock::getWarehouse, warehouse);
        }
        return stockMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    @Transactional
    public StockRecord stockOperate(StockOpRequest request) {
        if (!StringUtils.hasText(request.getMaterialCode())) {
            throw new BusinessException("物料编码不能为空");
        }
        if (request.getQty() == null || request.getQty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("操作数量必须大于0");
        }
        String bizType = StringUtils.hasText(request.getBizType()) ? request.getBizType().toUpperCase() : null;
        if (bizType == null || !bizType.matches("IN|OUT|ISSUE|RETURN")) {
            throw new BusinessException("业务类型非法，仅支持 IN/OUT/ISSUE/RETURN");
        }
        String warehouse = StringUtils.hasText(request.getWarehouse()) ? request.getWarehouse() : DEFAULT_WAREHOUSE;

        MaterialStock stock = stockMapper.selectByMatAndWh(request.getMaterialCode(), warehouse);
        if (stock == null) {
            stock = new MaterialStock();
            stock.setMaterialCode(request.getMaterialCode());
            stock.setWarehouse(warehouse);
            stock.setQty(BigDecimal.ZERO);
            stock.setUpdateTime(new Date());
            stockMapper.insert(stock);
        }

        boolean increase = "IN".equals(bizType) || "RETURN".equals(bizType);
        BigDecimal delta = increase ? request.getQty() : request.getQty().negate();
        BigDecimal newQty = stock.getQty().add(delta);
        if (newQty.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("库存不足，当前库存: " + stock.getQty());
        }
        stock.setQty(newQty);
        stock.setUpdateTime(new Date());
        stockMapper.updateById(stock);

        StockRecord record = new StockRecord();
        record.setMaterialCode(request.getMaterialCode());
        record.setWarehouse(warehouse);
        record.setBizType(bizType);
        record.setQty(request.getQty());
        record.setOrderNo(request.getOrderNo());
        record.setOperator(request.getOperator());
        record.setCreateTime(new Date());
        stockRecordMapper.insert(record);
        return record;
    }

    @Override
    public IPage<StockRecord> pageStockRecords(long current, long size, String materialCode) {
        LambdaQueryWrapper<StockRecord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(materialCode)) {
            wrapper.eq(StockRecord::getMaterialCode, materialCode);
        }
        wrapper.orderByDesc(StockRecord::getCreateTime);
        return stockRecordMapper.selectPage(new Page<>(current, size), wrapper);
    }
}
