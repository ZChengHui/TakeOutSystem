package com.to.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.to.reggie.entity.ShoppingCart;
import com.to.reggie.mapper.IShoppingCartMapper;
import com.to.reggie.service.IShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<IShoppingCartMapper, ShoppingCart> implements IShoppingCartService {
}
