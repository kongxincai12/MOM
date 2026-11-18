package com.simplemes.material.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.simplemes.material.dto.StockOpRequest;
import com.simplemes.material.entity.Material;
import com.simplemes.material.entity.MaterialStock;
import com.simplemes.material.entity.StockRecord;

/**
 * 物料与库存服务。
 */
public interface MaterialService {

    Material createMaterial(Material material);

    Material updateMaterial(Material material);

    void deleteMaterial(String id);

    IPage<Material> pageMaterial(long current, long size, String keyword);

    IPage<MaterialStock> pageStock(long current, long size, String materialCode, String warehouse);

    /** 入库 / 出库 / 领料 / 退料 */
    StockRecord stockOperate(StockOpRequest request);

    IPage<StockRecord> pageStockRecords(long current, long size, String materialCode);
}
