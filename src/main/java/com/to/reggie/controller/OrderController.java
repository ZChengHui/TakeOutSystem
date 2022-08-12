package com.to.reggie.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.to.reggie.common.R;
import com.to.reggie.entity.Orders;
import com.to.reggie.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderController extends BaseController{

    @Autowired
    public IOrderService iOrderService;


    /**
     * 订单管理分页查询
     * @return
     */
    @GetMapping("/page")
    public R<IPage<Orders>> getPage(Map map) {
        log.info(map.toString());
        IPage<Orders> iPage = new Page<>();
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
