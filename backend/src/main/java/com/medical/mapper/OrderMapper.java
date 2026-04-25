package com.medical.mapper;

import com.medical.entity.Order;
import com.medical.entity.QuarterlyOrderDTO;
import com.medical.entity.SearchForm;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface OrderMapper {
    @Select("select DISTINCT Customer from orders where Employee = #{name}")
    List<String> getCustomer(SearchForm searchForm);

    @Select("select * from orders where Employee = #{name} and Status = '进行中'")
    List<Order> findAll(SearchForm searchForm);

    List<Order> search(SearchForm searchForm);

    @Select("select * from orders where Remark = #{remark}")
    Order findByRemark(String remark);

    void addOrder(Order order);

    @Select("update orders set TotalAmount = #{totalAmount} where Remark = #{remark}")
    void setTotalAmount(Order order);

    @Update("update orders set Status = '已完成' where Remark = #{remark}")
    void editStatus(Order order);

    @Delete("delete from orders where Remark = #{remark}")
    void delete(Order order);


    @Select("SELECT " +
            "CASE " +
            "   WHEN MONTH(OrderDate) BETWEEN 1 AND 3 THEN '第一季度' " +
            "   WHEN MONTH(OrderDate) BETWEEN 4 AND 6 THEN '第二季度' " +
            "   WHEN MONTH(OrderDate) BETWEEN 7 AND 9 THEN '第三季度' " +
            "   ELSE '第四季度' " +
            "END AS quarter, " +
            "COUNT(OrderID) AS orderCount, " +
            "SUM(TotalAmount) AS totalAmount " +
            "FROM orders " +
            "WHERE Status = '已完成' " +
            "GROUP BY quarter " +
            "ORDER BY FIELD(quarter, '第一季度', '第二季度', '第三季度', '第四季度')")
    @Results({
            @Result(column = "quarter", property = "quarter"),
            @Result(column = "orderCount", property = "orderCount"),
            @Result(column = "totalAmount", property = "totalAmount")
    })
    List<QuarterlyOrderDTO> selectQuarterlyOrderStats();
}
