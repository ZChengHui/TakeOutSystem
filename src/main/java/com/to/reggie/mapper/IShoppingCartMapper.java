package com.to.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.to.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
