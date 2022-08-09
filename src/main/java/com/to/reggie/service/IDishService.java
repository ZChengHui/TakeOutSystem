package com.to.reggie.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.to.reggie.dto.DishDto;
import com.to.reggie.entity.Dish;

public interface IDishService extends IService<Dish> {

    //更新菜品及对应口味
    void updateWithFlavor(DishDto dishDto, Long operationId);

    //查询菜品信息以及口味 多表操作
    DishDto getByIdWithFlavor(Long id);

    //新增菜品，操作dish、dish_flavor表
    void saveWithFlavor(DishDto dishDto, Long operationId);

    //分页查询菜品
    IPage<DishDto> getDishPage(int cur, int pageSize, Dish dish);

}
