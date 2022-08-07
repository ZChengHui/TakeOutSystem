package com.to.reggie.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.to.reggie.common.R;
import com.to.reggie.entity.Employee;
import com.to.reggie.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/employee")
public class EmployeeController extends BaseController {

    @Autowired
    private IEmployeeService iEmployeeService;

    /**
     * 员工登录
     * @param session
     * @param employee
     * @return
     */
    @PostMapping("/login")//username存入session
    public R<Employee> login(@RequestBody Employee employee, HttpSession session) {
        Employee data = iEmployeeService.login(employee);
        session.setAttribute("employeeId", data.getId());
        return R.success(data);
    }

    /**
     * 员工登出
     * @param session
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpSession session) {
        session.removeAttribute("employeeId");
        return R.success("登出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @param session
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpSession session) {
        //看前端传值到后端是否可行//log.info("新增员工{}",employee.toString());
        Long operationId = (Long) session.getAttribute("employeeId");
        iEmployeeService.saveEmployee(employee, operationId);
        return R.success("员工注册成功");
    }

    /**
     * 员工信息分页查询
     */
    //@RequestBody用于Post请求
    @GetMapping("/page")
    public R<IPage<Employee>> getEmployeeList(int page, int pageSize, Employee employee) {

        //后段链接前端传值//log.info("####page:{},pageSize:{},name:{},username:{}",page,pageSize,employee.getName(),employee.getUsername());
        IPage iPage = iEmployeeService.getEmployeeList(page, pageSize, employee);
        // 当前页大于最大页 取小的值
        if (page > iPage.getPages()) {
            iPage = iEmployeeService.getEmployeeList((int)iPage.getPages(), pageSize, employee);
        }
        return R.success(iPage);
    }

    /**
     * 修改员工
     * @param employee
     * @return
     */
    //
    @PutMapping
    public R<String> change(@RequestBody Employee employee, HttpSession session) {
        log.info(employee.toString());
        Long operationId = (Long)session.getAttribute("employeeId");
        iEmployeeService.update(employee, operationId);
        return R.success("修改成功");
    }

    /**
     * 查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee employee = iEmployeeService.getEmployeeById(id);
        return R.success(employee);
    }

}
