package com.to.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.to.reggie.entity.Dish;
import com.to.reggie.mapper.IDishMapper;
import com.to.reggie.service.IDishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<IDishMapper, Dish> implements IDishService {

}
