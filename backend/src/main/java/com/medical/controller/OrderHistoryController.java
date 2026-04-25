package com.medical.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.medical.entity.Order;
import com.medical.entity.Result;
import com.medical.entity.SearchForm;
import com.medical.service.HistoryOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/historyorder")
public class OrderHistoryController {

    @Autowired
    private HistoryOrderService historyOrderService;

    @PostMapping
    public Result findAllHistory(@RequestBody SearchForm searchForm){
        log.info("传入的数据为,{}",searchForm);
        PageHelper.startPage(searchForm.getPageNum(), searchForm.getPageSize());
        List<Order> list = historyOrderService.findAllHistory(searchForm);
        PageInfo<Order> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @PostMapping("/customers")
    public Result findAllCustomers(@RequestBody SearchForm searchForm){
        log.info("传入的数据为,{}",searchForm);
        List<String> list = historyOrderService.findAllCustomers(searchForm);
        return Result.success(list);
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody Order order){
        historyOrderService.delete(order);
        return Result.success();
    }

    @PostMapping("/batch")
    public Result batchDelete(@RequestBody List<Order> list){
        for(Order order : list ){
            historyOrderService.delete(order);
        }
        return Result.success();
    }
}
