package com.to.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.to.reggie.entity.DishFlavor;
import com.to.reggie.mapper.IDishFlavorMapper;
import com.to.reggie.service.IDishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<IDishFlavorMapper, DishFlavor> implements IDishFlavorService {
}
