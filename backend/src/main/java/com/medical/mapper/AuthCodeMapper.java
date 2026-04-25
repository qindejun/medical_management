package com.medical.mapper;

import com.medical.entity.AuthCode;
import com.medical.entity.Params;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AuthCodeMapper {
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    List<AuthCode> selectAll();

    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    List<AuthCode> search(Params params);

    @Update("update auth_codes set Role = #{role} where codeID = #{codeId}")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    void updateCode(Params params);

    @Update("update employees set Role = #{role} where EmployeeID = (select UsedBy from auth_codes where CodeID = #{codeId})")
    void updateEmp(Params params);

    @Insert("insert into auth_codes (AuthCode, Role, UsedBy) values (#{authcode},#{role},#{employeeId})")
    void insert(@Param("authcode") String authcode,@Param("role") Integer role,@Param("employeeId") Integer employeeId);

    @Delete("delete from auth_codes where codeID = #{codeId}")
    void deleteById(Integer codeId);

    AuthCode selectById(@Param("codeId") Integer codeId);

    @Select("select CodeID from auth_codes where UsedBy = #{id}")
    Integer selectByUsedBy(Integer id);
}
