package com.to.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.to.reggie.entity.*;
import com.to.reggie.mapper.IAddressBookMapper;
import com.to.reggie.mapper.IOrderMapper;
import com.to.reggie.service.*;
import com.to.reggie.service.ex.AddressBookNotFoundException;
import com.to.reggie.service.ex.ShoppingCartEmptyException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<IOrderMapper, Orders> implements IOrderService {

    @Autowired
    private IShoppingCartService iShoppingCartService;


    @Autowired
    private IUserService iUserService;

    @Autowired
    private IAddressBookService iAddressBookService;

    @Autowired
    private IOrderDetailService iOrderDetailService;

    /**
     * 用户下单
     * 多表操作
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders, Long userId) {
        //查询当前用户购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> list = iShoppingCartService.list(queryWrapper);
        if (list == null || list.size() == 0) {
            throw new ShoppingCartEmptyException("购物车为空");
        }

        //查询用户信息
        User user = iUserService.getById(userId);
        //查询地址
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = iAddressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new AddressBookNotFoundException("地址不存在");
        }

        //插入order 一条数据
        Long orderId = IdWorker.getId();//订单号

        //原子操作 线程安全
        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = list.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        this.save(orders);

        //插入orderdetail 多条数据
        iOrderDetailService.saveBatch(orderDetails);

        //清空购物车
        iShoppingCartService.remove(queryWrapper);
    }
}
