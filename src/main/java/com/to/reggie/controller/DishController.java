package com.to.reggie.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.to.reggie.common.R;
import com.to.reggie.dto.DishDto;
import com.to.reggie.entity.Dish;
import com.to.reggie.service.IDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;

/**
 * 菜品管理 菜品口味管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController extends BaseController{

    @Autowired
    private IDishService iDishService;

    /**
     * 新增菜品
     * 操作两张表
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> saveDish(@RequestBody DishDto dishDto, HttpSession session) {//数据传输复杂对象
        Long operationId = (Long) session.getAttribute("employeeId");
        log.info(dishDto.toString());
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
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto, HttpSession session) {
        Long operationId = (Long) session.getAttribute("employeeId");
        iDishService.updateWithFlavor(dishDto, operationId);
        log.info(dishDto.toString());
        return R.success("修改菜品成功");
    }

}
