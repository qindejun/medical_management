package com.medical.mapper;

import com.medical.entity.Order;
import com.medical.entity.SearchForm;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HistoryOrderMapper {

    List<Order> findAllHistory(SearchForm searchForm);

    List<String> findAllCustomers(SearchForm searchForm);

    @Delete("delete from orders where Remark = #{remark}")
    void delete(Order order);
}
