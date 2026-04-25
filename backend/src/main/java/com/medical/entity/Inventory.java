package com.medical.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {
    private Integer InventoryID;
    private String deviceName;
    private String modelNumber;
    private String category;
    private String manufacturer;
    private Integer quantity;
    private String location;
    private LocalDateTime lastUpdated;
}
