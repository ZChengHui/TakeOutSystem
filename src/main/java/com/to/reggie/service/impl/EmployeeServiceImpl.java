package com.to.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.to.reggie.common.BaseContext;
import com.to.reggie.entity.Employee;
import com.to.reggie.mapper.IEmployeeMapper;
import com.to.reggie.service.IEmployeeService;
import com.to.reggie.service.ex.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<IEmployeeMapper, Employee> implements IEmployeeService {

    @Autowired
    private IEmployeeMapper iEmployeeMapper;

    /**
     * 登录
     * @param employee
     * @return
     */
    @Override
    public Employee login(Employee employee) {
        //md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //查询用户是否存在
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = iEmployeeMapper.selectOne(queryWrapper);
        if (emp == null) {
            throw new UserNotFoundException("员工不存在");
        }

        //验证密码
        if (!emp.getPassword().equals(password)) {
            throw new PasswordNotMatchException("密码错误");
        }

        //验证状态
        if (emp.getStatus() == 0) {
            throw new UserStatusException("用户被禁用");
        }

        //返回查询信息
        return emp;
    }

    /**
     * 员工注册
     * @param employee
     * @param operationId
     */
    @Override
    public void saveEmployee(Employee employee, Long operationId) {

        //判断重复记录
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee temp = iEmployeeMapper.selectOne(queryWrapper);
        if (temp != null) {
            //用户已存在
            throw new UsernameDuplicatedException("账号已存在");
        }

        //设置身份证后6位为初始密码
        String IdNumberSub = employee.getIdNumber();
        int len = IdNumberSub.length();
        String pwd = DigestUtils.md5DigestAsHex(IdNumberSub.substring(len-6).getBytes());
        employee.setPassword(pwd);

        //填充初始字段值
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //操作者的id
        //employee.setCreateUser(operationId);
        //employee.setUpdateUser(operationId);
        //log.info(employee.toString());
        BaseContext.setCurrentId(operationId);

        //执行插入
        int rows = iEmployeeMapper.insert(employee);
        if (rows != 1) {
            throw new InsertException("注册时产生未知异常");
        }
    }

    /**
     * 多条件分页查询
     * @param cur
     * @param size
     * @param employee
     * @return
     */
    @Override
    public IPage<Employee> getEmployeeList(int cur, int size, Employee employee) {
        //多条件模糊查询
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //（条件，字段名，具体值）
        queryWrapper.like(Strings.isNotEmpty(employee.getUsername()), Employee::getUsername, employee.getUsername());
        queryWrapper.like(Strings.isNotEmpty(employee.getName()), Employee::getName, employee.getName());
        queryWrapper.eq(Strings.isNotEmpty(employee.getSex()), Employee::getSex, employee.getSex());
        queryWrapper.like(Strings.isNotEmpty(employee.getPhone()), Employee::getPhone, employee.getPhone());
        queryWrapper.eq(employee.getStatus() != null, Employee::getStatus, employee.getStatus());
        queryWrapper.like(Strings.isNotEmpty(employee.getIdNumber()), Employee::getIdNumber, employee.getIdNumber());

        //添加排序
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //按日期等等

        IPage<Employee> page = new Page(cur, size);
        iEmployeeMapper.selectPage(page, queryWrapper);
        return page;
    }

    /**
     * 修改状态
     * @param employee
     */
    @Override
    public void update(Employee employee, Long operationId) {


        //判断重复记录
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee temp = iEmployeeMapper.selectOne(queryWrapper);
        Employee temp_ = iEmployeeMapper.selectById(employee.getId());
        //log.info(String.valueOf((temp.getId()==temp_.getId())));
        if (temp != null && !temp_.getId().equals(temp.getId())) {
            //用户已存在
            throw new UsernameDuplicatedException("账号已存在");
        }

        //填充字段
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(operationId);

        //利用线程维护同一执行流上的局部变量
        //Long id = Thread.currentThread().getId();
        //log.info("线程ID={}",id);
        //log.info("当前登录用户ID={}",operationId);
        BaseContext.setCurrentId(operationId);


        int rows = iEmployeeMapper.updateById(employee);
        if (rows != 1) {
            throw new UpdateException("修改时产生未知异常");
        }
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @Override
    public Employee getEmployeeById(Long id) {
        Employee employee = iEmployeeMapper.selectById(id);
        if (employee == null) {
            throw new UserNotFoundException("用户不存在");
        } else {
            return employee;
        }
    }

}
