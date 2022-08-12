package com.to.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.to.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IOrderMapper extends BaseMapper<Orders> {
}
