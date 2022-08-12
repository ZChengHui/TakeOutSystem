package com.to.reggie.controller;

import com.to.reggie.service.IOrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/orderDetail")
public class OrderDetailController extends BaseController{

    @Autowired
    private IOrderDetailService iOrderDetailService;

}
