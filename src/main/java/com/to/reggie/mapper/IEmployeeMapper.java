package com.to.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.to.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IEmployeeMapper extends BaseMapper<Employee> {
}
