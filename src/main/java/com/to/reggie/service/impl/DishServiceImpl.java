package com.to.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.to.reggie.common.BaseContext;
import com.to.reggie.dto.DishDto;
import com.to.reggie.entity.Category;
import com.to.reggie.entity.Dish;
import com.to.reggie.entity.DishFlavor;
import com.to.reggie.mapper.IDishMapper;
import com.to.reggie.service.ICategoryService;
import com.to.reggie.service.IDishFlavorService;
import com.to.reggie.service.IDishService;
import com.to.reggie.service.ex.DishNameDuplicatedException;
import com.to.reggie.service.ex.SetmealCanNotUpdateException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<IDishMapper, Dish> implements IDishService {

    //映射路径
    @Value("${fileIO.path}")
    private String basePath;

    @Autowired
    private IDishFlavorService iDishFlavorService;

    @Autowired
    private IDishMapper iDishMapper;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 批量删除
     * 多表操作
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBach(List<Long> ids) {

        //判断
        List<Dish> dishList = this.listByIds(ids);
        Boolean flag = true;
        for (Dish dish : dishList) {
            if (dish.getStatus() == 1) {
                flag = false;
                break;
            }
        }
        if (!flag) {
            throw new SetmealCanNotUpdateException("菜品正在销售不可删除");
        }

        //获取图片名
        List<String> imgList = new ArrayList<>();
        for (Dish dish : dishList) {
            imgList.add(dish.getImage());
        }

        //删除图片
        for (String fileName : imgList) {
            File file = new File(basePath + fileName);
            file.delete();
        }

        //删除dish
        this.removeByIds(ids);

        //删除dish_flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId, ids);
        iDishFlavorService.remove(queryWrapper);

    }

    /**
     * 批量修改菜品状态
     * @param state
     * @param ids
     * @param operationId
     */
    @Override
    public void updateStatus(int state, List<Long> ids, Long operationId) {
        //线程传值
        BaseContext.setCurrentId(operationId);

        //查找实体列表
        List<Dish> dishList = this.listByIds(ids);

        //更新状态 赋值
        for (Dish dish : dishList) {
            dish.setStatus(state);
        }

        this.updateBatchById(dishList);
    }

    /**
     * 更新菜品及对应口味
     * 多表操作
     * @param dishDto
     * @param operationId
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto, Long operationId) {
        //查重
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getName, dishDto.getName());
        Dish temp = iDishMapper.selectOne(queryWrapper);
        Dish temp_ = iDishMapper.selectById(dishDto.getId());
        if (temp != null && !temp_.getId().equals(temp.getId())) {
            throw new DishNameDuplicatedException("菜品名称重复");
        }

        //填充，利用线程传值
        BaseContext.setCurrentId(operationId);

        //更新dish表
        this.updateById(dishDto);

        //删除原来的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper_ = new LambdaQueryWrapper<>();
        queryWrapper_.eq(DishFlavor::getDishId, dishDto.getId());
        iDishFlavorService.remove(queryWrapper_);

        //添加现在的口味，赋值预处理
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        iDishFlavorService.saveBatch(flavors);

    }

    /**
     * 查询菜品信息以及口味
     * 多表操作
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询dish
        Dish dish = this.getById(id);

        //查询flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> list = iDishFlavorService.list(queryWrapper);

        //赋值
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);
        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 新增菜品，操作dish、dish_flavor表
     * @param dishDto
     */
    @Override
    @Transactional//开启数据库事务，启动类也要加事务注解
    public void saveWithFlavor(DishDto dishDto, Long operationId) {
        //查重
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getName, dishDto.getName());
        int counts = iDishMapper.selectCount(queryWrapper);
        if (counts != 0) {
            throw new DishNameDuplicatedException("菜品名称重复");
        }

        //填充，利用线程传值
        BaseContext.setCurrentId(operationId);

        //先保存菜品到dish
        this.save(dishDto);

        //遍历集合传dish_id
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }

        //保存到dish_flavor 批量保存
        iDishFlavorService.saveBatch(dishDto.getFlavors());
    }

    /**
     * 分页查询菜品
     * @param cur
     * @param pageSize
     * @param dish
     * @return
     */
    @Override
    public IPage<DishDto> getDishPage(int cur, int pageSize, Dish dish) {

        //动态多条件查询
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(dish.getName()!=null, Dish::getName, dish.getName());
        queryWrapper.eq(dish.getStatus()!=null, Dish::getStatus, dish.getStatus());
        queryWrapper.eq(dish.getCategoryId()!=null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //分页构造器对象
        IPage<Dish> page = new Page<>(cur, pageSize);
        IPage<DishDto> dtoIPage = new Page<>(cur, pageSize);//为了传递categoryName到前端

        iDishMapper.selectPage(page, queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(page, dtoIPage, "records");

        //records重新写入
        List<Dish> records = page.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();
            Category category = iCategoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();

                dishDto.setCategoryName(categoryName);
            }
            return dishDto;

        }).collect(Collectors.toList());


        dtoIPage.setRecords(list);

        return dtoIPage;
    }
}
