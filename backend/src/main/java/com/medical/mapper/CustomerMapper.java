package com.medical.mapper;

import com.medical.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CustomerMapper {
    @Select("select CompanyName from customers")
    List<String> getAll();

    void add(Customer customer);
}
