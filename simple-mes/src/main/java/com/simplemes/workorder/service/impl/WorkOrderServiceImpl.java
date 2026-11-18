package com.simplemes.workorder.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplemes.common.BusinessException;
import com.simplemes.workorder.entity.WorkOrder;
import com.simplemes.workorder.mapper.WorkOrderMapper;
import com.simplemes.workorder.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {

    /** 状态常量 */
    private static final String CREATED = "CREATED";
    private static final String RELEASED = "RELEASED";
    private static final String STARTED = "STARTED";
    private static final String FINISHED = "FINISHED";
    private static final String CLOSED = "CLOSED";

    @Autowired
    private WorkOrderMapper workOrderMapper;

    @Override
    @Transactional
    public WorkOrder create(WorkOrder workOrder) {
        if (!StringUtils.hasText(workOrder.getOrderNo())) {
            throw new BusinessException("工单号不能为空");
        }
        if (workOrderMapper.selectByOrderNo(workOrder.getOrderNo()) != null) {
            throw new BusinessException("工单号已存在: " + workOrder.getOrderNo());
        }
        workOrder.setId(null);
        workOrder.setStatus(CREATED);
        workOrder.setFinishQty(BigDecimal.ZERO);
        if (workOrder.getPlanQty() == null) {
            workOrder.setPlanQty(BigDecimal.ZERO);
        }
        workOrder.setCreateTime(new Date());
        workOrder.setUpdateTime(new Date());
        workOrderMapper.insert(workOrder);
        return workOrder;
    }

    @Override
    @Transactional
    public WorkOrder update(WorkOrder workOrder) {
        WorkOrder db = requireOrder(workOrder.getId());
        if (FINISHED.equals(db.getStatus()) || CLOSED.equals(db.getStatus())) {
            throw new BusinessException("工单已完工/关闭，不允许修改");
        }
        workOrder.setStatus(db.getStatus());
        workOrder.setFinishQty(db.getFinishQty());
        workOrder.setCreateTime(db.getCreateTime());
        workOrder.setUpdateTime(new Date());
        workOrderMapper.updateById(workOrder);
        return workOrder;
    }

    @Override
    @Transactional
    public void delete(String id) {
        WorkOrder db = requireOrder(id);
        if (STARTED.equals(db.getStatus()) || FINISHED.equals(db.getStatus())) {
            throw new BusinessException("工单已开工/完工，不允许删除");
        }
        workOrderMapper.deleteById(id);
    }

    @Override
    public WorkOrder get(String id) {
        return requireOrder(id);
    }

    @Override
    public IPage<WorkOrder> page(long current, long size, String status, String keyword) {
        LambdaQueryWrapper<WorkOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(WorkOrder::getStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(WorkOrder::getOrderNo, keyword)
                    .or().like(WorkOrder::getProductName, keyword)
                    .or().like(WorkOrder::getProductCode, keyword));
        }
        wrapper.orderByDesc(WorkOrder::getCreateTime);
        return workOrderMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    @Transactional
    public void release(String id) {
        transfer(id, CREATED, RELEASED, null);
    }

    @Override
    @Transactional
    public void start(String id) {
        WorkOrder db = transfer(id, RELEASED, STARTED, null);
        db.setStartDate(new Date());
        workOrderMapper.updateById(db);
    }

    @Override
    @Transactional
    public void finish(String id) {
        WorkOrder db = transfer(id, STARTED, FINISHED, null);
        db.setEndDate(new Date());
        workOrderMapper.updateById(db);
    }

    @Override
    @Transactional
    public void close(String id) {
        transfer(id, FINISHED, CLOSED, null);
    }

    @Override
    @Transactional
    public void increaseFinishQty(String orderNo, BigDecimal qty) {
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        WorkOrder db = workOrderMapper.selectByOrderNo(orderNo);
        if (db == null) {
            throw new BusinessException("工单不存在: " + orderNo);
        }
        BigDecimal finish = db.getFinishQty() == null ? BigDecimal.ZERO : db.getFinishQty();
        db.setFinishQty(finish.add(qty));
        db.setUpdateTime(new Date());
        workOrderMapper.updateById(db);
    }

    /**
     * 通用状态流转：校验当前状态必须为 expectFrom，然后置为 targetStatus。
     */
    private WorkOrder transfer(String id, String expectFrom, String targetStatus, String msg) {
        WorkOrder db = requireOrder(id);
        if (!expectFrom.equals(db.getStatus())) {
            throw new BusinessException(StringUtils.hasText(msg) ? msg
                    : "当前状态[" + db.getStatus() + "]不允许流转到[" + targetStatus + "]");
        }
        db.setStatus(targetStatus);
        db.setUpdateTime(new Date());
        workOrderMapper.updateById(db);
        return db;
    }

    private WorkOrder requireOrder(String id) {
        WorkOrder db = workOrderMapper.selectById(id);
        if (db == null) {
            throw new BusinessException("工单不存在: " + id);
        }
        return db;
    }
}
