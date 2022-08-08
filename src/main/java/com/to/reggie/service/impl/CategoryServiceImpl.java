package com.to.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.to.reggie.common.BaseContext;
import com.to.reggie.entity.Category;
import com.to.reggie.entity.Dish;
import com.to.reggie.entity.Setmeal;
import com.to.reggie.mapper.ICategoryMapper;
import com.to.reggie.mapper.IDishMapper;
import com.to.reggie.mapper.ISetmealMapper;
import com.to.reggie.service.ICategoryService;
import com.to.reggie.service.ex.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<ICategoryMapper, Category> implements ICategoryService {

    @Autowired
    private ICategoryMapper iCategoryMapper;

    @Autowired
    private IDishMapper iDishMapper;

    @Autowired
    private ISetmealMapper iSetmealMapper;

    /**
     * 新建分类
     * @param category
     * @param operationId
     */
    @Override
    public void addCategory(Category category, Long operationId) {
        //查重
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, category.getName());
        int temp = iCategoryMapper.selectCount(queryWrapper);

        if (temp != 0) {
            throw new CategoryDuplicatedException("该分类已存在");
        }

        //填充，利用线程传值
        BaseContext.setCurrentId(operationId);
        int rows = iCategoryMapper.insert(category);

        if (rows != 1) {
            throw new InsertException("注册分类产生未知异常");
        }
    }

    /**
     * 多条件分页查询菜品套餐分类表
     * @param cur
     * @param pageSize
     * @param category
     * @return
     */
    @Override
    public IPage<Category> getCategoryList(int cur, int pageSize, Category category) {

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Strings.isNotEmpty(category.getName()), Category::getName, category.getName());
        queryWrapper.eq(category.getType()!=null, Category::getType, category.getType());
        queryWrapper.orderByAsc(Category::getSort);
        queryWrapper.orderByDesc(Category::getUpdateTime);

        Page<Category> page = new Page<>(cur, pageSize);
        iCategoryMapper.selectPage(page, queryWrapper);

        return page;
    }

    /**
     * 根据id删除菜品套餐，有外键依赖条件判断
     * @param id
     */
    @Override
    public void removeCategory(Long id) {

        //查询是否关联了菜品
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, id);
        int dish = iDishMapper.selectCount(queryWrapper);

        //查询是否关联了套餐
        LambdaQueryWrapper<Setmeal> queryWrapper_ = new LambdaQueryWrapper<>();
        queryWrapper_.eq(Setmeal::getCategoryId, id);
        int setmeal = iSetmealMapper.selectCount(queryWrapper_);

        //判断执行删除
        if (dish != 0 || setmeal != 0) {
            throw new ForeignKeyConstraintException("该分类与其他菜品或套餐有关联，不可删除");
        }
        int rows = iCategoryMapper.deleteById(id);
        if (rows != 1) {
            throw new DeleteException("删除时产生未知异常");
        }
    }

    /**
     * 修改菜品套餐信息
     * @param category
     * @param operationId
     */
    @Override
    public void editCategory(Category category, Long operationId) {
        //查重
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, category.getName());
        Category temp = iCategoryMapper.selectOne(queryWrapper);
        Category temp_ = iCategoryMapper.selectById(category.getId());

        //比较逻辑不同于添加和删除
        if (temp != null && !temp_.getId().equals(temp.getId())) {
            throw new CategoryDuplicatedException("该分类已存在");
        }

        //填充，利用线程传值
        BaseContext.setCurrentId(operationId);
        int rows = iCategoryMapper.updateById(category);

        if (rows != 1) {
            throw new UpdateException("修改时产生未知异常");
        }

    }

}
