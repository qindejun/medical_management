package com.medical.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.medical.entity.Employee;
import com.medical.entity.Params;
import com.medical.entity.Password;
import com.medical.entity.Result;
import com.medical.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
@CrossOrigin
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public Result findAll(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize){
        log.info("参数为,{},{}",pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<Employee> list = employeeService.findAll();
        PageInfo<Employee> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @GetMapping("/search")
    public Result findBySearch(Params params) {
        PageInfo<Employee> pageInfo = employeeService.findBySearch(params);
        return Result.success(pageInfo);
    }

    @PutMapping("/update")
    public Result update(@RequestBody Employee employee) {
        log.info("修改员工信息,数据为:{}",employee);
        employeeService.update(employee);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id){
        employeeService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/batch")
    public Result batchDelete(@RequestBody ArrayList<Integer> ids){
        log.info("批量删除,接收到的id为,{}",ids);
        for(Integer id : ids ){
            employeeService.deleteById(id);
        }
        return Result.success();
    }

    @PostMapping("/changePassword")
    public Result passwordChange(@RequestBody Password password){
        log.info("修改密码,传入的数据为,{}",password);
        employeeService.changePassword(password);
        return Result.success();
    }

}
