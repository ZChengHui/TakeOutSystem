package com.to.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.to.reggie.common.R;
import com.to.reggie.dto.OrdersDto;
import com.to.reggie.entity.Orders;
import com.to.reggie.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderController extends BaseController{

    @Autowired
    public IOrderService iOrderService;

    /**
     * 用户订单分页查询
     * @return
     */
    @GetMapping("/userPage")
    public R<IPage<OrdersDto>> getuserPage(int page, int pageSize, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        IPage<OrdersDto> iPage = iOrderService.getUserOrdersPage(page, page, userId);
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
