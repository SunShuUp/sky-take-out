package com.sky.service;

import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 增加员工
     * @param employee
     */
    void saveEmployee(Employee employee);

    /**
     * 分也查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult page(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 通过id查询员工信息
     * @param id
     * @return
     */
    Employee getById(Long id);

    /**
     * 更改员工信息
     * @param employee
     */
    void update(Employee employee);

    /**
     * 修改密码
     * @param passwordEditDTO
     */
    void eitePassword(PasswordEditDTO passwordEditDTO);
}
