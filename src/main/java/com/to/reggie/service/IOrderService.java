package com.to.reggie.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.to.reggie.dto.OrdersDto;
import com.to.reggie.entity.Orders;

public interface IOrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    void submit(Orders orders, Long userId);

    /**
     * 分页查用户订单
     * @return
     */
    IPage<OrdersDto> getUserOrdersPage(int page, int pageSize, Long userId);

}
