package com.to.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.to.reggie.entity.Orders;

public interface IOrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    void submit(Orders orders, Long userId);

}
