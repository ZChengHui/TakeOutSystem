package com.to.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.to.reggie.common.BaseContext;
import com.to.reggie.common.R;
import com.to.reggie.entity.DishFlavor;
import com.to.reggie.entity.ShoppingCart;
import com.to.reggie.service.IShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController extends BaseController{

    @Autowired
    private IShoppingCartService iShoppingCartService;

    /**
     * 单独删除购物车数据
     * @return
     */
    @DeleteMapping("/delete")
    public R<String> delete(@RequestBody ShoppingCart shoppingCart, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        //菜品、套餐判断
        if (shoppingCart.getDishId() == null) {
            Long setmealId = Long.valueOf(shoppingCart.getSetmealId().toString());
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
        } else {
            String flavor = shoppingCart.getDishFlavor().toString();
            Long dishId = Long.valueOf(shoppingCart.getDishId().toString());
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
            queryWrapper.eq(ShoppingCart::getDishFlavor, flavor);
        }
        iShoppingCartService.remove(queryWrapper);
        return R.success("删除成功");
    }


    /**
     * 减少购物车商品数量
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody Map map, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Object dish = map.get("dishId");
        Object setmeal = map.get("setmealId");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        //菜品、套餐判断
        if (dish == null) {
            Long setmealId = Long.valueOf(setmeal.toString());
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId);
        } else {
            String flavor = map.get("dishFlavor").toString();
            Long dishId = Long.valueOf(dish.toString());
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
            queryWrapper.eq(ShoppingCart::getDishFlavor, flavor);
        }
        ShoppingCart shoppingCart = iShoppingCartService.getOne(queryWrapper);
        //数量减一
        int number = shoppingCart.getNumber();
        if (number > 0) {
            shoppingCart.setNumber(number - 1);
        }
        iShoppingCartService.updateById(shoppingCart);
        return R.success(shoppingCart);
    }


    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        iShoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }


    /**
     * 查询购物车信息
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        queryWrapper.orderByDesc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = iShoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 添加购物车
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session) {
        log.info(shoppingCart.toString());
        //设置用户ID
        Long userId = (Long) session.getAttribute("userId");
        shoppingCart.setUserId(userId);

        //查询当前菜品或购物车是否在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());

        if (dishId != null) {
            //添加的是菜品 判断口味和dishid
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
            queryWrapper.eq(ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
        } else {
            //添加的是套餐 判断setmealid
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //如果已经存在，数量加一
        ShoppingCart one = iShoppingCartService.getOne(queryWrapper);
        if (one != null) {
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            iShoppingCartService.updateById(one);
        } else {
            //如果不存在，则添加到购物车
            shoppingCart.setCreateTime(LocalDateTime.now());
            iShoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }
        return R.success(one);
    }


}
