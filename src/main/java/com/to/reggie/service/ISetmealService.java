package com.to.reggie.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.to.reggie.dto.SetmealDto;
import com.to.reggie.entity.Setmeal;

import java.util.List;


public interface ISetmealService extends IService<Setmeal> {

    //新增套餐
    void saveWithDish(SetmealDto setmealDto, Long operationId);

    //多条件分页查询套餐
    IPage<SetmealDto> getSetmealPage(int page, int pageSize, Setmeal setmeal);

    //修改套餐状态
    void updateStatus(int state, List<Long> ids, Long operationId);

    //批量删除 多表操作
    void deleteBach(List<Long> ids);

}
