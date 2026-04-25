package com.medical.service;

import com.medical.entity.AuthCode;
import com.medical.entity.Employee;
import com.medical.exception.CustomException;
import com.medical.mapper.AuthCodeMapper;
import com.medical.mapper.EmployeeMapper;
import com.medical.mapper.LoginMapper;
import com.medical.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Slf4j
@Service
public class LoginService {

    @Resource
    private LoginMapper loginMapper;

    @Resource
    private AuthCodeMapper authCodeMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    public Employee Login(Employee employee) throws NoSuchAlgorithmException {
        Employee emp = loginMapper.Login(employee);
        if(emp == null){
            throw new CustomException("账号或密码错误");
        }
        String token = JwtTokenUtils.generalToken(emp.getEmployeeId().toString(),emp.getPassword());
        emp.setToken(token);
        return emp;
    }

    public AuthCode selectRole(AuthCode authCode) {
        AuthCode code = loginMapper.selectRoleByCode(authCode);
        if(code == null){
            throw new CustomException("授权码错误!");
        }
        return code;
    }

    public Employee Register(Employee employee) {
        loginMapper.Register(employee);
        Employee emp = employeeMapper.getById(employee.getEmployeeId());
        if(emp == null){
            throw new CustomException("注册失败，请重试!");
        }
        loginMapper.UpdateCodeStatus(employee);
        loginMapper.setUsedAt(employee);
        String token = JwtTokenUtils.generalToken(emp.getEmployeeId().toString(),emp.getPassword());
        emp.setToken(token);
        return emp;
    }
}
