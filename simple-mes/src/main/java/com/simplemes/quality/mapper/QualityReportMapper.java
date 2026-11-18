package com.simplemes.quality.mapper;

import com.simplemes.quality.dto.QualityReportDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 质量报表聚合查询：按工单汇总合格 / 报废 / 返工数量。
 */
@Mapper
public interface QualityReportMapper {

    @Select("<script>" +
            "SELECT wo.ORDER_NO AS order_no, " +
            "       wo.PRODUCT_NAME AS product_name, " +
            "       wo.PLAN_QTY AS plan_qty, " +
            "       NVL(pr.Q, 0) AS qualified_qty, " +
            "       NVL(sr.Q, 0) AS scrap_qty, " +
            "       NVL(rw.Q, 0) AS rework_qty " +
            "FROM SMES_WORK_ORDER wo " +
            "LEFT JOIN (SELECT ORDER_NO, SUM(QUALIFIED_QTY) Q FROM SMES_PRODUCTION_REPORT GROUP BY ORDER_NO) pr ON pr.ORDER_NO = wo.ORDER_NO " +
            "LEFT JOIN (SELECT ORDER_NO, SUM(QTY) Q FROM SMES_SCRAP_RECORD GROUP BY ORDER_NO) sr ON sr.ORDER_NO = wo.ORDER_NO " +
            "LEFT JOIN (SELECT ORDER_NO, SUM(QTY) Q FROM SMES_REWORK_RECORD GROUP BY ORDER_NO) rw ON rw.ORDER_NO = wo.ORDER_NO " +
            "<where>" +
            "  <if test=\"orderNo != null and orderNo != ''\"> wo.ORDER_NO = #{orderNo} </if>" +
            "</where>" +
            "ORDER BY wo.CREATE_TIME DESC" +
            "</script>")
    List<QualityReportDto> selectQualityReport(@Param("orderNo") String orderNo);
}
