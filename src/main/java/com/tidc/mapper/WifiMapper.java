package com.tidc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tidc.entity.Wifi;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface WifiMapper extends BaseMapper<Wifi> {
    @Select("select * from wifi")
    List<Wifi> selectAll();

    @Update("update wifi set del_flag = 0 where wifi_name = #{wifiName}")
    void updateDelFlag(@Param("wifiName") String wifiName);

}
