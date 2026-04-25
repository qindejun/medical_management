package com.medical.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.medical.entity.*;
import com.medical.mapper.InventoryMapper;
import com.medical.mapper.InventoryTransactionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class InventoryService {

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private InventoryTransactionMapper inventoryTransactionMapper;

    public List<Inventory> findAll() {
        return inventoryMapper.findAll();
    }

    public PageInfo<Inventory> search(Params params) {
        // 确保分页参数有效，设置默认值
        int pageNum = params.getPageNum() != null ? params.getPageNum() : 1;
        int pageSize = params.getPageSize() != null ? params.getPageSize() : 5;
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 执行查询
        List<Inventory> list = inventoryMapper.search(params);
        // 构造 PageInfo
        return new PageInfo<>(list);
    }

    public List<Inventory> searchAll() {
        return inventoryMapper.searchAll();
    }

    public List<String> searchManufacturer(Params params) {
        String nameModel = params.getNameModel();
        if (nameModel != null && nameModel.contains("-")) {
            String[] parts = nameModel.split("-", 2);
            params.setDeviceName(parts[0]);
            params.setModelNumber(parts[1]);
        }
        return inventoryMapper.selectManufacturer(params);
    }

    public List<String> searchWarehouse(Params params) {
        String nameModel = params.getNameModel();
        if (nameModel != null && nameModel.contains("-")) {
            String[] parts = nameModel.split("-", 2);
            params.setDeviceName(parts[0]);
            params.setModelNumber(parts[1]);
        } else{
            return inventoryMapper.findAllWarehouse();
        }
        return inventoryMapper.selectWarehouse(params);
    }

    public Integer selectQuantity(Params params) {
        String nameModel = params.getNameModel();
        if (nameModel != null && nameModel.contains("-")) {
            String[] parts = nameModel.split("-", 2);
            params.setDeviceName(parts[0]);
            params.setModelNumber(parts[1]);
        }
        return inventoryMapper.selectQuantity(params);
    }


    @Transactional(rollbackFor = Exception.class)
    public void out(Params params) {
        String nameModel = params.getNameModel();
        if (nameModel != null && nameModel.contains("-")) {
            String[] parts = nameModel.split("-", 2);
            params.setDeviceName(parts[0]);
            params.setModelNumber(parts[1]);
        }
        params.setDeviceId(inventoryMapper.getDeviceID(params));
        params.setWarehouseId(inventoryMapper.getWarehouseId(params));
        params.setDateTime(LocalDateTime.now());
        inventoryMapper.out(params);
        if (inventoryMapper.selectByIds(params) == 0){
            inventoryMapper.deleteByIds(params);
        }
        inventoryTransactionMapper.out(params);
    }

    public List<Device> searchAllIn() {
        return inventoryMapper.searchAllIn();
    }

    @Transactional(rollbackFor = Exception.class)
    public void in(Params params) {
        String nameModel = params.getNameModel();
        if (nameModel != null && nameModel.contains("-")) {
            String[] parts = nameModel.split("-", 2);
            params.setDeviceName(parts[0]);
            params.setModelNumber(parts[1]);
        }
        params.setDeviceId(inventoryMapper.getDeviceID(params));
        params.setWarehouseId(inventoryMapper.getWarehouseId(params));
        params.setDateTime(LocalDateTime.now());
        inventoryMapper.in(params);
        inventoryTransactionMapper.in(params);
    }

    @Transactional(rollbackFor = Exception.class)
    public void transfer(Params params) {
        params.setWarehouse(params.getOutWarehouse());
        String nameModel = params.getNameModel();
        if (nameModel != null && nameModel.contains("-")) {
            String[] parts = nameModel.split("-", 2);
            params.setDeviceName(parts[0]);
            params.setModelNumber(parts[1]);
        }
        params.setDeviceId(inventoryMapper.getDeviceID(params));
        params.setWarehouseId(inventoryMapper.getWarehouseId(params));
        params.setInWarehouseId(inventoryMapper.getinWarehouseId(params));
        params.setDateTime(LocalDateTime.now());
        inventoryMapper.out(params);
        Integer quantity = inventoryMapper.selectByIds(params);
        log.info("查询到的数量为:{}",quantity);
        if( quantity != null ){
            if (quantity != 0) {
                inventoryMapper.transfer(params);
            } else {
                inventoryMapper.deleteByIds(params);
            }
        } else {
            inventoryMapper.in(params);
        }
        inventoryTransactionMapper.transfer(params);
    }

    @Transactional(rollbackFor = Exception.class)
    public void edit(Params params) {
        Integer beforeEdit = inventoryMapper.selectQuantityById(params);
        params.setBeforeEdit(beforeEdit);
        params.setDateTime(LocalDateTime.now());
        inventoryMapper.edit(params);
        inventoryTransactionMapper.Edit(params);
    }

    public List<InventoryTransaction> findAllTransaction() {
        return inventoryTransactionMapper.selectAll();
    }

    public PageInfo<InventoryTransaction> searchTransaction(SearchForm searchForm) {
        log.info("查询参数:{}",searchForm);
        // 确保分页参数有效，设置默认值
        int pageNum = searchForm.getPageNum() != null ? searchForm.getPageNum() : 1;
        int pageSize = searchForm.getPageSize() != null ? searchForm.getPageSize() : 5;
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 执行查询
        List<InventoryTransaction> list = inventoryTransactionMapper.search(searchForm);
        // 构造 PageInfo
        return new PageInfo<>(list);
    }

    public void deleteById(Integer transactionId) {
        inventoryTransactionMapper.deleteById(transactionId);
    }

    public List<String> findAllNames() {
        return inventoryMapper.findAllNames();
    }

    public List<String> findAllWarehouses(ProductForm productForm) {
        log.info("Service层调用");
        return inventoryMapper.findAllWarehouses(productForm);
    }

    public ProductForm selectPriceAndStock(ProductForm productForm) {
        log.info("Service层调用");
        return inventoryMapper.selectPriceAndStock(productForm);
    }

    public List<Map<String, Object>> getInventorySummaryByCategory() {
        return inventoryMapper.selectCategoryInventorySummary();
    }
}
