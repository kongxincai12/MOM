package com.simplemes.quality.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.simplemes.common.BusinessException;
import com.simplemes.quality.dto.QualityReportDto;
import com.simplemes.quality.entity.ReworkRecord;
import com.simplemes.quality.entity.ScrapRecord;
import com.simplemes.quality.mapper.QualityReportMapper;
import com.simplemes.quality.mapper.ReworkRecordMapper;
import com.simplemes.quality.mapper.ScrapRecordMapper;
import com.simplemes.quality.service.QualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class QualityServiceImpl implements QualityService {

    private static final List<String> REWORK_STATUS = Arrays.asList("PENDING", "DOING", "DONE");
    private static final BigDecimal HUNDRED = new BigDecimal("100");

    @Autowired
    private ScrapRecordMapper scrapRecordMapper;
    @Autowired
    private ReworkRecordMapper reworkRecordMapper;
    @Autowired
    private QualityReportMapper qualityReportMapper;

    @Override
    @Transactional
    public ScrapRecord createScrap(ScrapRecord record) {
        if (!StringUtils.hasText(record.getOrderNo())) {
            throw new BusinessException("工单号不能为空");
        }
        if (record.getQty() == null || record.getQty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("报废数量必须大于0");
        }
        record.setId(null);
        if (record.getScrapTime() == null) {
            record.setScrapTime(new Date());
        }
        record.setCreateTime(new Date());
        scrapRecordMapper.insert(record);
        return record;
    }

    @Override
    public IPage<ScrapRecord> pageScrap(long current, long size, String orderNo) {
        LambdaQueryWrapper<ScrapRecord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(orderNo)) {
            wrapper.eq(ScrapRecord::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(ScrapRecord::getCreateTime);
        return scrapRecordMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    @Transactional
    public ReworkRecord createRework(ReworkRecord record) {
        if (!StringUtils.hasText(record.getOrderNo())) {
            throw new BusinessException("工单号不能为空");
        }
        if (record.getQty() == null || record.getQty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("返工数量必须大于0");
        }
        record.setId(null);
        if (!StringUtils.hasText(record.getStatus())) {
            record.setStatus("PENDING");
        }
        if (record.getReworkDate() == null) {
            record.setReworkDate(new Date());
        }
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        reworkRecordMapper.insert(record);
        return record;
    }

    @Override
    @Transactional
    public ReworkRecord updateReworkStatus(String id, String status) {
        ReworkRecord db = reworkRecordMapper.selectById(id);
        if (db == null) {
            throw new BusinessException("返工记录不存在: " + id);
        }
        if (status == null || !REWORK_STATUS.contains(status.toUpperCase())) {
            throw new BusinessException("返工状态非法，仅支持 PENDING/DOING/DONE");
        }
        db.setStatus(status.toUpperCase());
        db.setUpdateTime(new Date());
        reworkRecordMapper.updateById(db);
        return db;
    }

    @Override
    public IPage<ReworkRecord> pageRework(long current, long size, String orderNo) {
        LambdaQueryWrapper<ReworkRecord> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(orderNo)) {
            wrapper.eq(ReworkRecord::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(ReworkRecord::getCreateTime);
        return reworkRecordMapper.selectPage(new Page<>(current, size), wrapper);
    }

    @Override
    public List<QualityReportDto> qualityReport(String orderNo) {
        List<QualityReportDto> list = qualityReportMapper.selectQualityReport(orderNo);
        for (QualityReportDto dto : list) {
            BigDecimal qualified = nvl(dto.getQualifiedQty());
            BigDecimal scrap = nvl(dto.getScrapQty());
            BigDecimal total = qualified.add(scrap);
            if (total.compareTo(BigDecimal.ZERO) > 0) {
                dto.setPassRate(qualified.multiply(HUNDRED).divide(total, 2, RoundingMode.HALF_UP));
                dto.setScrapRate(scrap.multiply(HUNDRED).divide(total, 2, RoundingMode.HALF_UP));
            } else {
                dto.setPassRate(BigDecimal.ZERO);
                dto.setScrapRate(BigDecimal.ZERO);
            }
        }
        return list;
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
