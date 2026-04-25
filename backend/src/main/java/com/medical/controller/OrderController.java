package com.medical.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.medical.entity.*;
import com.medical.service.DeviceService;
import com.medical.service.OrderService;
import com.medical.utils.RandomRemark;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static javafx.beans.binding.Bindings.or;
import static javafx.beans.binding.Bindings.when;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/customers")
    public Result getCustomer(@RequestBody SearchForm searchForm){
        log.info("传入的姓名为,{}",searchForm);
        List<String> list = orderService.getCustomer(searchForm);
        return Result.success(list);
    }

    @PostMapping
    public Result findAll(@RequestBody SearchForm searchForm){
        log.info("传入的数据为,{}",searchForm);
        PageHelper.startPage(searchForm.getPageNum(), searchForm.getPageSize());
        List<Order> list = orderService.findAll(searchForm);
        PageInfo<Order> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @PostMapping("/search")
    public Result search(@RequestBody SearchForm searchForm){
        log.info("传入的数据为,{}",searchForm);
        PageHelper.startPage(searchForm.getPageNum(), searchForm.getPageSize());
        List<Order> list = orderService.search(searchForm);
        PageInfo<Order> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody AddOrder addOrder){
        log.info("传入的数据为,{}",addOrder);
        String remark;
        while(true){
            remark = RandomRemark.generateRandomString();
            Order order = orderService.findByRemark(remark);
            if(order == null){
                break;
            }
        }
        BigDecimal totalAmount = BigDecimal.valueOf(0);
        for(Order order : addOrder.getItems()){
            order.setCustomer(addOrder.getCustomer());
            order.setEmployee(addOrder.getEmployee());
            order.setRemark(remark);
            order.setCategory(deviceService.selectCategoryByName(order.getDeviceName()));
            order.setTotalAmount(BigDecimal.valueOf(0));
            order.setStatus("进行中");
            order.setOrderDate(LocalDateTime.now());
            log.info("订单的数据为:{}",order);
            totalAmount = totalAmount.add(BigDecimal.valueOf(order.getQuantity()).multiply(order.getUnitPrice()));
            orderService.addOrder(order);
        }
        Order order = new Order();
        order.setTotalAmount(totalAmount);
        order.setRemark(remark);
        orderService.setTotalAmount(order);
        return Result.success();
    }

    @PutMapping("/edit")
    public Result editStatus(@RequestBody Order order){
        log.info("Remark:{}",order.getRemark());
        orderService.editStatus(order);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody Order order){
        orderService.delete(order);
        return Result.success();
    }

    @GetMapping("/echarts/bie")
    public Result getQuarterlyOrderStats() {
        List<QuarterlyOrderDTO> data = orderService.getQuarterlyOrderStats();
        return Result.success(data);
    }

}
