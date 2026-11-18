package com.simplemes.material.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.simplemes.material.entity.Material;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MaterialMapper extends BaseMapper<Material> {

    @Select("SELECT * FROM SMES_MATERIAL WHERE MATERIAL_CODE = #{materialCode}")
    Material selectByCode(@Param("materialCode") String materialCode);
}
