package com.medical.service;

import com.medical.entity.Order;
import com.medical.entity.QuarterlyOrderDTO;
import com.medical.entity.SearchForm;
import com.medical.mapper.OrderMapper;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    public List<String> getCustomer(SearchForm searchForm) {
        List<String> list = orderMapper.getCustomer(searchForm);
        return list;
    }

    public List<Order> findAll(SearchForm searchForm) {
         return orderMapper.findAll(searchForm);
    }

    public List<Order> search(SearchForm searchForm) {
        return orderMapper.search(searchForm);
    }

    public Order findByRemark(String remark) {
        return orderMapper.findByRemark(remark);
    }

    public void addOrder(Order order) {
        orderMapper.addOrder(order);
    }

    public void setTotalAmount(Order order) {
        orderMapper.setTotalAmount(order);
    }

    public void editStatus(Order order) {
        log.info("修改状态的Remark,{}",order.getRemark());
        orderMapper.editStatus(order);
    }

    public void delete(Order order) {
        orderMapper.delete(order);
    }

    public List<QuarterlyOrderDTO> getQuarterlyOrderStats() {
        return orderMapper.selectQuarterlyOrderStats();
    }
}
