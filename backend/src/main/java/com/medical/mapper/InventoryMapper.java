package com.medical.mapper;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageInfo;
import com.medical.entity.Device;
import com.medical.entity.Inventory;
import com.medical.entity.Params;
import com.medical.entity.ProductForm;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;


import java.util.List;
import java.util.Map;

@Repository
public interface InventoryMapper extends Mapper<Inventory> {
    List<Inventory> findAll();

    List<Inventory> search(Params params);

    List<Inventory> searchAll();

    @Select("select Manufacturer from medicaldevices where DeviceName = #{deviceName} and ModelNumber = #{modelNumber}")
    List<String> selectManufacturer(Params params);

    @Select("SELECT\n" +
            "            w.Location\n" +
            "        FROM\n" +
            "            medicaldevices md\n" +
            "                INNER JOIN\n" +
            "            inventory inv ON md.DeviceID = inv.DeviceID\n" +
            "                INNER JOIN\n" +
            "            warehouses w ON inv.WarehouseID = w.WarehouseID\n" +
            "        WHERE\n" +
            "            md.DeviceName = #{deviceName}\n" +
            "          AND md.ModelNumber = #{modelNumber}\n" +
            "          and md.Manufacturer = #{manufacturer}")
    List<String> selectWarehouse(Params params);

    @Select("SELECT inv.Quantity\n" +
            "        FROM medicaldevices md\n" +
            "                 INNER JOIN\n" +
            "             inventory inv ON md.DeviceID = inv.DeviceID\n" +
            "                 INNER JOIN\n" +
            "             warehouses w ON inv.WarehouseID = w.WarehouseID\n" +
            "        WHERE md.DeviceName = #{deviceName}\n" +
            "          AND md.ModelNumber = #{modelNumber}\n" +
            "          and md.Manufacturer = #{manufacturer}\n" +
            "          and w.Location = #{warehouse}")
    Integer selectQuantity(Params params);

    @Select("select DeviceID from medicaldevices where DeviceName = #{deviceName} and ModelNumber = #{modelNumber} and Manufacturer = #{manufacturer}\n")
    Integer getDeviceID(Params params);


    void out(Params params);

    @Select("select WarehouseID from warehouses where Location = #{warehouse}")
    Integer getWarehouseId(Params params);

    @Select("select Quantity from inventory where DeviceID = #{deviceId} and WarehouseID = #{warehouseId}")
    Integer selectByIds(Params params);

    @Delete("delete from inventory where DeviceID = #{deviceId} and WarehouseID = #{warehouseId}")
    void deleteByIds(Params params);

    @Select("select * from medicaldevices")
    List<Device> searchAllIn();

    @Select("select Location from warehouses")
    List<String> findAllWarehouse();

    void in(Params params);

    void transfer(Params params);

    @Select("select WarehouseID from warehouses  where Location = #{inWarehouse}")
    String getinWarehouseId(Params params);

    Params selectById(Integer inventoryID);

    @Update("update inventory set Quantity = #{quantity} where InventoryID = #{inventoryID}")
    void edit(Params params);

    @Select("select Quantity from inventory where InventoryID = #{inventoryID}")
    Integer selectQuantityById(Params params);

    @Select("select DISTINCT DeviceName from medicaldevices join inventory on medicaldevices.DeviceID = inventory.DeviceID")
    List<String> findAllNames();

    @Select("SELECT w.Location\n" +
            "FROM warehouses w\n" +
            "         INNER JOIN inventory i ON i.WarehouseID = w.WarehouseID\n" +
            "         INNER JOIN medicaldevices md ON i.DeviceID = md.DeviceID\n" +
            "WHERE md.DeviceName = #{deviceName}\n" +
            "  AND md.ModelNumber = #{modelNumber}\n" +
            "  AND md.Manufacturer = #{manufacturer};")
    List<String> findAllWarehouses(ProductForm productForm);

    @Select("SELECT md.UnitPrice, i.Quantity AS stock\n" +
            "        FROM medicaldevices md\n" +
            "                 INNER JOIN inventory i ON md.DeviceID = i.DeviceID\n" +
            "                 INNER JOIN warehouses w ON i.`WarehouseID` = w.`WarehouseID`\n" +
            "        WHERE md.DeviceName = #{deviceName} AND md.ModelNumber = #{modelNumber} AND md.Manufacturer = #{manufacturer} AND w.Location = #{warehouse}")
    ProductForm selectPriceAndStock(ProductForm productForm);

    @Select("SELECT md.category AS name, SUM(inv.quantity) AS value " +
            "FROM medicaldevices md " +
            "JOIN inventory inv ON md.DeviceID = inv.DeviceID " +
            "GROUP BY md.category")
    @Results({
            @Result(column = "name", property = "name"),
            @Result(column = "value", property = "value")
    })
    List<Map<String, Object>> selectCategoryInventorySummary();
}
