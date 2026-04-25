package com.medical.mapper;


import com.medical.entity.AuthCode;
import com.medical.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface LoginMapper{
    Employee Login(Employee employee);

    @Select("select * from auth_codes where AuthCode = #{authCode} ")
    AuthCode selectRoleByCode(AuthCode authCode);

    void Register(Employee employee);

    @Update("update auth_codes set Status = 0 where UsedBy = #{employeeId}")
    void UpdateCodeStatus(Employee employee);

    @Update("update auth_codes set UsedAt = CURRENT_TIMESTAMP where UsedBy = #{employeeId}")
    void setUsedAt(Employee employee);
}
