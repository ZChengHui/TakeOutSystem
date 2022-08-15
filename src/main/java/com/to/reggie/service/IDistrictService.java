package com.to.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.to.reggie.entity.District;

import java.util.List;

public interface IDistrictService extends IService<District> {

    //通过父代码查询其孩子
    List<District> getByParent(String parent);

    //获取名称
    String getNameByCode(String code);

}
