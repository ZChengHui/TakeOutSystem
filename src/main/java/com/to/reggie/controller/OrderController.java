package com.to.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.to.reggie.common.R;
import com.to.reggie.dto.OrdersDto;
import com.to.reggie.entity.Orders;
import com.to.reggie.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderController extends BaseController{

    @Autowired
    public IOrderService iOrderService;

    /**
     * 多条件查询订单
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public R<IPage<Orders>> getOrderPage(
            int page,
            int pageSize,
            String number,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")Date beginTime,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")Date endTime
    ) {
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(number!=null, Orders::getNumber, number);
        queryWrapper.ge(beginTime!=null, Orders::getOrderTime, beginTime);
        queryWrapper.le(endTime!=null, Orders::getOrderTime, endTime);

        IPage<Orders> iPage = new Page<>(page, pageSize);
        iOrderService.page(iPage, queryWrapper);
        return R.success(iPage);
    }

    /**
     * 用户订单分页查询
     * @return
     */
    @GetMapping("/userPage")
    public R<IPage<OrdersDto>> getuserPage(int page, int pageSize, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        IPage<OrdersDto> iPage = iOrderService.getUserOrdersPage(page, pageSize, userId);
        return R.success(iPage);
    }


    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        log.info(orders.toString());
        iOrderService.submit(orders,userId);
        return R.success("下单成功");
    }


}
