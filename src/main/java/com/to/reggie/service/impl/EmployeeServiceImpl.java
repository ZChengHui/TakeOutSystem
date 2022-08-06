package com.to.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.to.reggie.common.R;
import com.to.reggie.entity.Employee;
import com.to.reggie.mapper.IEmployeeMapper;
import com.to.reggie.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

@Service
public class EmployeeServiceImpl extends ServiceImpl<IEmployeeMapper, Employee> implements IEmployeeService {

    @Autowired
    private IEmployeeMapper iEmployeeMapper;

    @Override
    public R<Employee> login(HttpServletRequest request, Employee employee) {
        //md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //查询用户是否存在
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = iEmployeeMapper.selectOne(queryWrapper);
        if (emp == null) {
            return R.error("用户不存在");
        }

        //验证密码
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        //验证状态
        if (emp.getStatus() == 0) {
            return R.error("用户被禁用");
        }

        //登录成功 id存入session
        request.getSession().setAttribute("employeeId",emp.getId());
        return R.success(emp);
    }
}
