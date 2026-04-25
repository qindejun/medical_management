package com.medical.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryTransaction {
    private Integer transactionId;
    private String transactionType;
    private String deviceName;
    private String modelNumber;
    private Integer quantity;
    private String fromWarehouse;
    private String toWarehouse;
    private LocalDateTime transactionDate;
    private String employee;
    private Integer beforeEdit;
}
