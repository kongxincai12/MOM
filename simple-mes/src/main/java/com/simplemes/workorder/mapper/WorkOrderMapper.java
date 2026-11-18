package com.simplemes.workorder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplemes.workorder.entity.WorkOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WorkOrderMapper extends BaseMapper<WorkOrder> {

    /** 按工单号查询 */
    @Select("SELECT * FROM SMES_WORK_ORDER WHERE ORDER_NO = #{orderNo}")
    WorkOrder selectByOrderNo(@Param("orderNo") String orderNo);
}
