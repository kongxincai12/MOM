package com.simplemes.workorder.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.simplemes.workorder.entity.WorkOrder;

import java.math.BigDecimal;

/**
 * 生产工单服务：CRUD + 状态流转。
 */
public interface WorkOrderService {

    WorkOrder create(WorkOrder workOrder);

    WorkOrder update(WorkOrder workOrder);

    void delete(String id);

    WorkOrder get(String id);

    IPage<WorkOrder> page(long current, long size, String status, String keyword);

    /** 下发：CREATED -> RELEASED */
    void release(String id);

    /** 开工：RELEASED -> STARTED */
    void start(String id);

    /** 完工：STARTED -> FINISHED */
    void finish(String id);

    /** 关闭：FINISHED -> CLOSED */
    void close(String id);

    /** 累加已完成数量（供生产报工调用） */
    void increaseFinishQty(String orderNo, BigDecimal qty);
}
