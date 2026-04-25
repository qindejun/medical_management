package com.medical.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductForm {
    private String deviceName;
    private String modelNumber;
    private String manufacturer;
    private String warehouse;
    private Integer stock;
    private Integer quantity;
    private BigDecimal unitPrice;
}
