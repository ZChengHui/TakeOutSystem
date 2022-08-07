package com.to.reggie.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.to.reggie.entity.Employee;


public interface IEmployeeService extends IService<Employee> {

    /**
     * 登陆
     * @param employee
     * @return
     */
    Employee login(Employee employee);

    /**
     * 员工注册
     * @param employee
     * @param OperationId
     */
    void saveEmployee(Employee employee, Long OperationId);

    /**
     * 多条件分页查询
     * @param cur
     * @param size
     * @param employee
     * @return
     */
    IPage<Employee> getEmployeeList(int cur, int size, Employee employee);

    /**
     * 修改状态
     * @param employee
     * @param operationId
     */
    void update(Employee employee, Long operationId);

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    Employee getEmployeeById(Long id);

}
