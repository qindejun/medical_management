package com.medical.entity;

import ch.qos.logback.core.joran.action.AppenderRefAction;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Params {
    //医疗器械搜索
    private Integer deviceId;
    private String deviceName;
    private String category;
    private String manufacturer;

    //员工搜索
    private String name;
    private String department;

    //授权码搜索
    private Integer codeId;
    private Integer role;
    private Integer status;

    //库存搜索
    private Integer inventoryID;
    private String nameModel;
    private String modelNumber;
    private String warehouse;
    private Integer warehouseId;
    private Integer quantity;
    private Integer totalQuantity;
    private String outWarehouse;
    private String inWarehouse;
    private String inWarehouseId;
    private Integer beforeEdit;


    private LocalDateTime dateTime;
    private Integer pageNum;
    private Integer pageSize;
}
