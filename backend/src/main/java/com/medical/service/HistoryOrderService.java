package com.medical.service;

import com.medical.entity.Order;
import com.medical.entity.SearchForm;
import com.medical.mapper.HistoryOrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class HistoryOrderService {
    @Autowired
    private HistoryOrderMapper historyOrderMapper;

    public List<Order> findAllHistory(SearchForm searchForm) {
        return historyOrderMapper.findAllHistory(searchForm);
    }

    public List<String> findAllCustomers(SearchForm searchForm) {
        return  historyOrderMapper.findAllCustomers(searchForm);
    }

    public void delete(Order order) {
        historyOrderMapper.delete(order);
    }
}
