package com.medical.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.medical.entity.*;
import com.medical.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public Result findAll(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize){
        log.info("分页数据为:{},{}",pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<Inventory> list = inventoryService.findAll();
        PageInfo<Inventory> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @GetMapping("/search")
    public Result search(Params params){
        log.info("查询操作,传入的数据为:{}",params);
        PageInfo<Inventory> pageInfo = inventoryService.search(params);
        return Result.success(pageInfo);
    }

    @GetMapping("/searchall")
    public Result searchAll(){
        List<Inventory> list = inventoryService.searchAll();
        return Result.success(list);
    }

    @GetMapping("/manufacturer")
    public Result selectManufacturer(Params params){
        List<String> list = inventoryService.searchManufacturer(params);
        return Result.success(list);
    }

    @GetMapping("/warehouse")
    public Result selectWarehouse(Params params){
        log.info("查询仓库的条件为,{},{}",params.getNameModel(),params.getManufacturer());
        List<String> list = inventoryService.searchWarehouse(params);
        return Result.success(list);
    }

    @GetMapping("/quantity")
    public Result selectQuantity(Params params){
        log.info("查询库存的条件为,{},{},{}",params.getNameModel(),params.getManufacturer(),params.getWarehouse());
        return Result.success(inventoryService.selectQuantity(params));
    }

    @PutMapping("/out")
    public Result out(@RequestBody Params params){
        log.info("出库的数据为,{}",params);
        inventoryService.out(params);
        return Result.success();
    }

    @GetMapping("/searchallin")
    public Result searchAllIn(){
        List<Device> list = inventoryService.searchAllIn();
        return Result.success(list);
    }

    @PutMapping("/in")
    public Result in(@RequestBody Params params){
        log.info("入库的数据为,{}",params);
        inventoryService.in(params);
        return Result.success();
    }

    @PutMapping("/transfer")
    public Result transfer(@RequestBody Params params){
        log.info("调拨的数据为,{}",params);
        inventoryService.transfer(params);
        return Result.success();
    }

    @PutMapping("/edit")
    public Result edit(@RequestBody Params params){
        log.info("修改的数据为,{}",params);
        inventoryService.edit(params);
        return Result.success();
    }

    @GetMapping("/transaction")
    public Result findAllTransaction(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize){
        log.info("分页数据为:{},{}",pageNum,pageSize);
        PageHelper.startPage(pageNum, pageSize);
        List<InventoryTransaction> list = inventoryService.findAllTransaction();
        PageInfo<InventoryTransaction> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @PostMapping("/transaction/search")
    public Result searchTransaction(@RequestBody SearchForm searchForm){
        log.info("查询操作,传入的数据为:{}",searchForm);
        PageInfo<InventoryTransaction> pageInfo = inventoryService.searchTransaction(searchForm);
        return Result.success(pageInfo);
    }

    @PutMapping("/batch")
    public Result batchDelete(@RequestBody List<InventoryTransaction> list){
        for(InventoryTransaction i : list){
            inventoryService.deleteById(i.getTransactionId());
        }
        return Result.success();
    }

    @GetMapping("/devicenames")
    public Result findAllNames(){
        List<String> list = inventoryService.findAllNames();
        return Result.success(list);
    }

    @GetMapping("/warehouses")
    public Result findAllWarehouses(ProductForm productForm){
        log.info("前端传入的参数为,{}",productForm);
        List<String> list = inventoryService.findAllWarehouses(productForm);
        return Result.success(list);
    }

    @GetMapping("/stock")
    public Result selectPriceAndStock(ProductForm productForm){
        log.info("前端传入的参数为,{}",productForm);
        ProductForm p = inventoryService.selectPriceAndStock(productForm);
        return Result.success(p);
    }

    @GetMapping("/echarts/bie")
    public Result getInventorySummaryByCategory() {
        List<Map<String, Object>> result = inventoryService.getInventorySummaryByCategory();
        return Result.success(result);
    }
}
