package com.to.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.to.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ICategoryMapper extends BaseMapper<Category> {
}
