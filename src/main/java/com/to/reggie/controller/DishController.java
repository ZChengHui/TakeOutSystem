package com.to.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.to.reggie.common.R;
import com.to.reggie.dto.DishDto;
import com.to.reggie.entity.Category;
import com.to.reggie.entity.Dish;
import com.to.reggie.entity.DishFlavor;
import com.to.reggie.entity.Setmeal;
import com.to.reggie.service.ICategoryService;
import com.to.reggie.service.IDishFlavorService;
import com.to.reggie.service.IDishService;
import com.to.reggie.service.ex.SetmealCanNotUpdateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品管理 菜品口味管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController extends BaseController{

    @Autowired
    private IDishService iDishService;

    @Autowired
    private IDishFlavorService iDishFlavorService;
    
    @Autowired
    private ICategoryService iCategoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 按菜品分类id查询菜品列表 DTO版改进
     * 联动加载
     * @param dish
     * @return
     */
    @Cacheable(value = "dishCache", key = "#dish.categoryId + '_' + #dish.status")
    @GetMapping("/list")
    public R<List<DishDto>> getDishByCategoryId(Dish dish) {
//        List<DishDto> dishDtoList = null;
//
//        //从redis中获取缓存数据  dish_8374234782_1
//        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
//
//        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
//
//        //如果存在，直接返回，无需查询数据库
//        if (dishDtoList != null) {
//            return R.success(dishDtoList);
//        }

        //如果不存在，查询数据库，并添加缓存
        //查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);//启售状态
        queryWrapper.orderByAsc(Dish::getSort);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        List<Dish> list = iDishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            //设置关联表的分类名 赋值
            Long categoryId = item.getCategoryId();
            Category category = iCategoryService.getById(categoryId);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            //设置关联表的口味 赋值
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> list1 = iDishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(list1);
            
            return dishDto;
        }).collect(Collectors.toList());

//        //加入redis缓存
//        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }

    /**
     * 按菜品分类id查询菜品列表
     * 联动加载
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> getDishByCategoryId(Dish dish) {
//
//        //查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus, 1);//启售状态
//        queryWrapper.orderByAsc(Dish::getSort);
//        queryWrapper.orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = iDishService.list(queryWrapper);
//        return R.success(list);
//    }

    /**
     * 新增菜品
     * 操作两张表
     * @param dishDto
     * @return
     */
    @CacheEvict(value = "dishCache", allEntries = true)
    @PostMapping
    public R<String> saveDish(@RequestBody DishDto dishDto, HttpSession session) {//数据传输复杂对象
        Long operationId = (Long) session.getAttribute("employeeId");
        //log.info(dishDto.toString());
        iDishService.saveWithFlavor(dishDto, operationId);
        return R.success("新增菜品成功");
    }

    /**
     * 分页查询菜品
     * 多表操作 用DTO
     * @param cur
     * @param pageSize
     * @param dish
     * @return
     */
    @GetMapping("/page")
    public R<IPage<DishDto>> getDishPage(@RequestParam("page") int cur, int pageSize, Dish dish) {
        IPage<DishDto> page = iDishService.getDishPage(cur, pageSize, dish);
        return R.success(page);
    }

    /**
     * 根据id查询菜品及口味
     * 多表操作
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getDishWithFlavor(@PathVariable Long id) {
        DishDto dishDto = iDishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @param session
     * @return
     */
    @CacheEvict(value = "dishCache", allEntries = true)
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto, HttpSession session) {
        Long operationId = (Long) session.getAttribute("employeeId");
        iDishService.updateWithFlavor(dishDto, operationId);
        log.info(dishDto.toString());

        //清理所有缓存数据
        //Set keys = redisTemplate.keys("dish_*");//通配符
        Set keys = redisTemplate.keys("dish_" + dishDto.getCategoryId() + "_1");
        redisTemplate.delete(keys);//按规则清理

        return R.success("修改菜品成功");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @CacheEvict(value = "dishCache", allEntries = true)
    @DeleteMapping
    public R<String> deleteBach(@RequestParam List<Long> ids) {//自动解析前端ids
        //log.info(ids.toString());
        iDishService.deleteBach(ids);
        return R.success("删除成功");
    }

    /**
     * 批量更改菜品状态
     * @param state
     * @param ids
     * @return
     */
    @CacheEvict(value = "dishCache", allEntries = true)
    @PostMapping("/status/{state}")
    public R<String> updateStatus(@PathVariable int state, @RequestParam List<Long> ids, HttpSession session) {
        Long operationId = (Long) session.getAttribute("employeeId");
        iDishService.updateStatus(state, ids, operationId);
        return R.success("修改菜品状态成功");
    }

}
