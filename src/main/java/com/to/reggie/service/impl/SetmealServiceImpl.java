package com.to.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.to.reggie.common.BaseContext;
import com.to.reggie.dto.SetmealDto;
import com.to.reggie.entity.Category;
import com.to.reggie.entity.Dish;
import com.to.reggie.entity.Setmeal;
import com.to.reggie.entity.SetmealDish;
import com.to.reggie.mapper.ISetmealMapper;
import com.to.reggie.service.ICategoryService;
import com.to.reggie.service.ISetmealService;
import com.to.reggie.service.ISetmealDishService;
import com.to.reggie.service.ex.SetmealCanNotUpdateException;
import com.to.reggie.service.ex.SetmealDuplicationException;
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
public class SetmealServiceImpl extends ServiceImpl<ISetmealMapper, Setmeal> implements ISetmealService {

    //映射路径
    @Value("${fileIO.path}")
    private String basePath;

    @Autowired
    private ISetmealDishService iSetmealDishService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 新增套餐
     * 多表操作
     * @param setmealDto
     * @param operationId
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto, Long operationId) {
        //查重
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getName, setmealDto.getName());
        int counts = this.count(queryWrapper);
        if (counts != 0) {
            throw new SetmealDuplicationException("套餐名重复");
        }

        BaseContext.setCurrentId(operationId);

        //基本信息setmeal
        this.save(setmealDto);

        //关联信息setmeal_dish
        //赋值
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }

        iSetmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 多条件分页查询套餐
     * @param cur
     * @param pageSize
     * @param setmeal
     * @return
     */
    @Override
    public IPage<SetmealDto> getSetmealPage(int cur, int pageSize, Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(setmeal.getName()!=null, Setmeal::getName, setmeal.getName());
        queryWrapper.eq(setmeal.getCategoryId()!=null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        IPage<Setmeal> page = new Page<>(cur, pageSize);
        IPage<SetmealDto> dtoIPage = new Page<>(cur, pageSize);
        this.page(page, queryWrapper);

        //对象拷贝 将分类名称赋值
        List<Setmeal> records = page.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();
            Category category = iCategoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        //给分页对象数据
        dtoIPage.setRecords(list);

        return dtoIPage;
    }

    /**
     * 修改套餐状态
     * @param state
     * @param ids
     * @param operationId
     */
    @Override
    public void updateStatus(int state, List<Long> ids, Long operationId) {
        BaseContext.setCurrentId(operationId);

        //查找对应对象列表
        List<Setmeal> list = this.listByIds(ids);
        for (Setmeal setmeal : list) {
            //修改值
            setmeal.setStatus(state);
        }
        //批量修改
        this.updateBatchById(list);
    }

    /**
     * 批量删除
     * 多表操作
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBach(List<Long> ids) {
        //判断
        Boolean flag = true;
        List<Setmeal> setmealList = this.listByIds(ids);
        for (Setmeal setmeal : setmealList) {
            if (setmeal.getStatus() == 1) {
                flag = false;
                break;
            }
        }
        if (!flag) {
            throw new SetmealCanNotUpdateException("套餐正在销售不可删除");
        }

        //获取图片名
        List<String> imgList = new ArrayList<>();
        for (Setmeal setmeal : setmealList) {
            imgList.add(setmeal.getImage());
        }

        //删除图片
        for (String fileName : imgList) {
            File file = new File(basePath + fileName);
            file.delete();
        }

        //删除setmeal
        this.removeByIds(ids);

        //删除setmeal_dish
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getSetmealId, ids);
        iSetmealDishService.remove(queryWrapper);
    }
}


