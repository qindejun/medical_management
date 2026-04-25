package com.medical.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.medical.entity.AuthCode;
import com.medical.entity.Employee;
import com.medical.entity.Params;
import com.medical.exception.CustomException;
import com.medical.mapper.AuthCodeMapper;
import com.medical.mapper.EmployeeMapper;
import com.medical.utils.RandomStringGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class AuthCodeService {

    @Autowired
    private AuthCodeMapper authCodeMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    public List<AuthCode> findAll() {
        return authCodeMapper.selectAll();
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public PageInfo<AuthCode> search(Params params) {
        // 确保分页参数有效，设置默认值
        int pageNum = params.getPageNum() != null ? params.getPageNum() : 1;
        int pageSize = params.getPageSize() != null ? params.getPageSize() : 5;
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 执行查询
        List<AuthCode> list = authCodeMapper.search(params);
        // 构造 PageInfo
        return new PageInfo<>(list);
    }

    @Transactional
    public void update(Params params) {
        authCodeMapper.updateCode(params);
        authCodeMapper.updateEmp(params);
    }

    public void add(Integer role) {
        Employee emp = new Employee();
        emp.setRole(role);
        employeeMapper.AddEmployeeByRole(emp);
        String authcode = RandomStringGenerator.generateRandomString(16);
        log.info("授权码为:{}",authcode);
        authCodeMapper.insert(authcode,role,emp.getEmployeeId());
    }

    public void deleteById(Integer codeId) {
        AuthCode authCode = authCodeMapper.selectById(codeId);
        if (authCode.getStatus() == 1) {
            authCodeMapper.deleteById(codeId);
            employeeMapper.deleteById(authCode.getUsedBy());
        } else {
            throw new CustomException("该授权码绑定的员工还在任职，无法删除！");
        }
    }
}
