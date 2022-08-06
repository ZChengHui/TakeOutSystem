package com.to.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.to.reggie.common.R;
import com.to.reggie.entity.Employee;

import javax.servlet.http.HttpServletRequest;

public interface IEmployeeService extends IService<Employee> {
    public R<Employee> login(HttpServletRequest request, Employee employee);
}
