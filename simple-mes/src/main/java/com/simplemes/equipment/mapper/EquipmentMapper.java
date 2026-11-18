package com.simplemes.equipment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplemes.equipment.entity.Equipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EquipmentMapper extends BaseMapper<Equipment> {

    @Select("SELECT * FROM SMES_EQUIPMENT WHERE EQP_CODE = #{eqpCode}")
    Equipment selectByCode(@Param("eqpCode") String eqpCode);
}
