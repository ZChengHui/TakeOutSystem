package com.to.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.to.reggie.entity.OrderDetail;
import com.to.reggie.mapper.IOrderDetailMapper;
import com.to.reggie.service.IOrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<IOrderDetailMapper, OrderDetail> implements IOrderDetailService {
}
