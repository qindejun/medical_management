package com.medical.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.medical.entity.Device;
import com.medical.entity.Params;
import com.medical.entity.ProductForm;
import com.medical.entity.Result;
import com.medical.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping
    public Result findAll(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "5") Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<Device> list = deviceService.findAll();
        PageInfo<Device> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    @GetMapping("/search")
    public Result findBySearch(Params params) {
        PageInfo<Device> pageInfo = deviceService.findBySearch(params);
        return Result.success(pageInfo);
    }

    @PostMapping
    public Result AddDevice(@RequestBody Device device){
        log.info("新增医疗器械,数据为:{}",device);
        deviceService.AddDevice(device);
        return Result.success();
    }

    @PutMapping
    public Result UpdateDevice(@RequestBody Device device){
        log.info("修改医疗器械,数据为:{}",device);
        deviceService.UpdateDevice(device);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id){
        deviceService.deleteById(id);
        return Result.success();
    }

    @PutMapping("/batch")
    public Result batchDelete(@RequestBody List<Device> list){
        for(Device device : list ){
            deviceService.deleteById(device.getDeviceID());
        }
        return Result.success();
    }

    @GetMapping("/models")
    public Result findAllModels(String deviceName){
        List<String> list = deviceService.findAllModels(deviceName);
        return Result.success(list);
    }

    @GetMapping("/manufacturers")
    public Result findAllManufacturers(ProductForm productForm){
        List<String> list = deviceService.findAllManufacturers(productForm);
        return Result.success(list);
    }

    @GetMapping("/info")
    public Result getDeviceInfo(ProductForm productForm){
        List<String> list = deviceService.getDeviceInfo(productForm);
        return  Result.success(list);
    }
}
