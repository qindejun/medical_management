package com.medical.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.medical.entity.AuthCode;
import com.medical.entity.Employee;
import com.medical.entity.Params;
import com.medical.entity.Password;
import com.medical.exception.CustomException;
import com.medical.mapper.AuthCodeMapper;
import com.medical.mapper.EmployeeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private AuthCodeMapper authCodeMapper;

    public List<Employee> findAll() {
        return employeeMapper.selectAll();
    }

    public PageInfo<Employee> findBySearch(Params params) {
        // 确保分页参数有效，设置默认值
        int pageNum = params.getPageNum() != null ? params.getPageNum() : 1;
        int pageSize = params.getPageSize() != null ? params.getPageSize() : 5;
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 执行查询
        List<Employee> list = employeeMapper.selectBySearch(params);
        // 构造 PageInfo
        return new PageInfo<>(list);
    }

    public void update(Employee employee) {
        employeeMapper.update(employee);
    }

    public void deleteById(Integer id) {
        Integer codeId = authCodeMapper.selectByUsedBy(id);
        employeeMapper.deleteById(id);
        authCodeMapper.deleteById(codeId);
    }

    public Employee findById(Integer integer) {
        return employeeMapper.getById(integer);
    }

    public void changePassword(Password password) {
        if(password.getOldPassword().equals(employeeMapper.getById(password.getEmployeeId()).getPassword())){
            log.info("查找的密码为,{}",employeeMapper.getById(password.getEmployeeId()).getPassword());
            employeeMapper.changePassword(password);
        }else {
            throw new CustomException("原密码错误!");
        }
    }
}
