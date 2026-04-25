package com.medical.mapper;

import com.medical.entity.Device;
import com.medical.entity.ProductForm;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;
import com.medical.entity.Params;
import java.util.List;

@Repository
public interface DeviceMapper extends Mapper<Device> {
    @Select("select * from medicaldevices where status = 1")
    public List<Device> selectAll();

    List<Device> selectBySearch(@Param("params") Params params);

    @Update("update medicaldevices set status = 0 where DeviceID = #{id}")
    void deleteById(@Param("id") Integer id);

    void AddDevice(Device device);

    void UpdateDevice(Device device);

    Device selectByAdd(Device device);

    @Select("select DISTINCT ModelNumber from medicaldevices join inventory on inventory.DeviceID = medicaldevices.DeviceID and medicaldevices.DeviceName = #{deviceName}")
    List<String> findAllModels(String deviceName);

    @Select("select Manufacturer from medicaldevices where DeviceName = #{deviceName} and ModelNumber = #{modelNumber}")
    List<String> findAllManufacturers(ProductForm productForm);

    @Select("select * from medicaldevices where DeviceName = #{deviceName} and ModelNumber = #{modelNumber} and Manufacturer = #{manufacturer}")
    List<String> getDeviceInfo(ProductForm productForm);

    @Select("select Category from medicaldevices where DeviceName = #{deviceName}")
    String selectCategoryByName(String deviceName);
}
