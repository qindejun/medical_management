package com.medical.service;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.medical.entity.Device;
import com.medical.entity.Params;
import com.medical.entity.ProductForm;
import com.medical.exception.CustomException;
import com.medical.mapper.DeviceMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DeviceService {

    @Resource
    private DeviceMapper deviceMapper;

    public List<Device> findAll(){
        return deviceMapper.selectAll();
    }

    public PageInfo<Device> findBySearch(Params params) {
        // 确保分页参数有效，设置默认值
        int pageNum = params.getPageNum() != null ? params.getPageNum() : 1;
        int pageSize = params.getPageSize() != null ? params.getPageSize() : 5;
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 执行查询
        List<Device> list = deviceMapper.selectBySearch(params);
        // 构造 PageInfo
        return new PageInfo<>(list);
    }

    public void deleteById(Integer id) {
        deviceMapper.deleteById(id);
    }

    public void AddDevice(Device device) {
        Device dev = deviceMapper.selectByAdd(device);
        if(dev != null){
            throw new CustomException("该医疗器械已存在,请勿重复添加!");
        }
        deviceMapper.AddDevice(device);
    }

    public void UpdateDevice(Device device) {
        deviceMapper.UpdateDevice(device);
    }

    public List<String> findAllModels(String deviceName) {
        return deviceMapper.findAllModels(deviceName);
    }

    public List<String> findAllManufacturers(ProductForm productForm) {
        return deviceMapper.findAllManufacturers(productForm);
    }

    public List<String> getDeviceInfo(ProductForm productForm) {
        return deviceMapper.getDeviceInfo(productForm);
    }

    public String selectCategoryByName(String deviceName){
        return deviceMapper.selectCategoryByName(deviceName);
    }
}
