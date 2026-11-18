package com.simplemes.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplemes.material.entity.MaterialStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MaterialStockMapper extends BaseMapper<MaterialStock> {

    @Select("SELECT * FROM SMES_MATERIAL_STOCK WHERE MATERIAL_CODE = #{materialCode} AND WAREHOUSE = #{warehouse}")
    MaterialStock selectByMatAndWh(@Param("materialCode") String materialCode, @Param("warehouse") String warehouse);
}
