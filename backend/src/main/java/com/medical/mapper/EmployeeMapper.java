package com.medical.mapper;

import com.medical.entity.Employee;
import com.medical.entity.Params;
import com.medical.entity.Password;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface EmployeeMapper {
    @Select("select * from employees where Name is not null and EmployeeID = #{employeeId}")
    Employee getById(Integer employeeId);

    @Select("select * from employees where Name is not null")
    List<Employee> selectAll();

    List<Employee> selectBySearch(Params params);

    void update(Employee employee);

    @Delete("delete from employees where EmployeeID = #{id}")
    void deleteById(Integer id);

    void AddEmployeeByRole(Employee emp);

    void changePassword(Password password);
}
