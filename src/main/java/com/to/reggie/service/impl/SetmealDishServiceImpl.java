package com.to.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.to.reggie.entity.SetmealDish;
import com.to.reggie.mapper.ISetmealDishMapper;
import com.to.reggie.service.ISetmealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<ISetmealDishMapper, SetmealDish> implements ISetmealDishService{
}
