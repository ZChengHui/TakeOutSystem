package com.to.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.to.reggie.common.R;
import com.to.reggie.dto.SetmealDto;
import com.to.reggie.entity.Category;
import com.to.reggie.entity.Dish;
import com.to.reggie.entity.Setmeal;
import com.to.reggie.entity.SetmealDish;
import com.to.reggie.service.ICategoryService;
import com.to.reggie.service.ISetmealDishService;
import com.to.reggie.service.ISetmealService;
import com.to.reggie.service.ex.SetmealCanNotUpdateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController extends BaseController{

    @Autowired
    private ISetmealService iSetmealService;

    @Autowired
    private ICategoryService iCategoryService;

    @Autowired
    private ISetmealDishService iSetmealDishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 按套餐分类id查询套餐列表 DTO改进版
     * 联动加载
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<SetmealDto>> getDishByCategoryId(Setmeal setmeal) {
        List<SetmealDto> setmealDtoList = null;

        //设置套餐关键字
        String key = "setmeal_" + setmeal.getCategoryId() + "_" + setmeal.getStatus();

        setmealDtoList = (List<SetmealDto>) redisTemplate.opsForValue().get(key);

        //如果存在，直接返回，无需查询数据库
        if (setmealDtoList != null) {
            return R.success(setmealDtoList);
        }

        //如果不存在，查询数据库，并添加缓存
        //查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus, 1);//启售状态
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = iSetmealService.list(queryWrapper);

        //用流遍历
        setmealDtoList = list.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item, setmealDto);
            //赋值 分类名
            Long categoryId = item.getCategoryId();
            Category category = iCategoryService.getById(categoryId);
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }

            //赋值 套餐包含的所有菜品
            Long setmealId = item.getId();
            LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, setmealId);
            List<SetmealDish> dishList = iSetmealDishService.list(dishLambdaQueryWrapper);

            setmealDto.setSetmealDishes(dishList);

            return setmealDto;
        }).collect(Collectors.toList());

        //加入redis
        redisTemplate.opsForValue().set(key, setmealDtoList);

        return R.success(setmealDtoList);
    }

//    /**
//     * 按套餐分类id查询套餐列表
//     * 联动加载
//     * @param setmeal
//     * @return
//     */
//    @GetMapping("/list")
//    public R<List<Setmeal>> getDishByCategoryId(Setmeal setmeal) {
//
//        //查询条件
//        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
//        queryWrapper.eq(Setmeal::getStatus, 1);//启售状态
//        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
//
//        List<Setmeal> list = iSetmealService.list(queryWrapper);
//        return R.success(list);
//    }

    /**
     * 修改套餐状态
     * @param state
     * @param ids
     * @return
     */
    @PostMapping("/status/{state}")
    public R<String> updateStatus(@PathVariable int state, @RequestParam List<Long> ids, HttpSession session) {
        Long operationId = (Long) session.getAttribute("employeeId");
        iSetmealService.updateStatus(state, ids, operationId);
        return R.success("修改套餐状态成功");
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteBach(@RequestParam List<Long> ids) {
        iSetmealService.deleteBach(ids);
        return R.success("删除成功");
    }

    /**
     * 添加套餐
     * @param setmealDto
     * @param session
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto, HttpSession session) {
        Long operationId = (Long) session.getAttribute("employeeId");
        //log.info(setmealDto.toString());
        iSetmealService.saveWithDish(setmealDto, operationId);
        return R.success("新增套餐成功");
    }

    /**
     * 分页查询套餐
     * @param page
     * @param pageSize
     * @param setmeal
     * @return
     */
    @GetMapping("/page")
    public R<IPage<SetmealDto>> getSetmealPage(int page, int pageSize, Setmeal setmeal) {
        //log.info(setmeal.toString());
        IPage<SetmealDto> iPage = iSetmealService.getSetmealPage(page, pageSize, setmeal);
        return R.success(iPage);
    }

    /**
     * 查询套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmealById(@PathVariable Long id) {
        SetmealDto setmealDto = iSetmealService.getSetmealWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐信息
     * @param setmealDto
     * @param session
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto, HttpSession session) {
        Long operationId = (Long) session.getAttribute("employeeId");
        iSetmealService.updateWithDish(setmealDto, operationId);
        log.info(setmealDto.toString());

        //清理所有缓存数据
        Set keys = redisTemplate.keys("setmeal_" + setmealDto.getCategoryId() + "_1");//通配符
        redisTemplate.delete(keys);//按规则清理

        return R.success("修改菜品成功");
    }

}
