package com.to.reggie.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.to.reggie.entity.Category;

public interface ICategoryService extends IService<Category> {

    /**
     * 新建分类
     * @param category
     * @param operationId
     */
    void addCategory(Category category, Long operationId);

    /**
     * 分页查询菜品套餐分类表
     * @param cur
     * @param pageSize
     * @param category
     * @return
     */
    IPage<Category> getCategoryList(int cur, int pageSize, Category category);

    /**
     * 根据ID删除菜品套餐分类
     * @param id
     */
    void removeCategory(Long id);

    /**
     * 修改菜品套餐信息
     * @param category
     * @param operationId
     */
    void editCategory(Category category, Long operationId);
}
