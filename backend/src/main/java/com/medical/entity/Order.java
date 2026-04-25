package com.medical.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    private Integer orderId;
    private String customer;
    private String deviceName;
    private String modelNumber;
    private String category;
    private String manufacturer;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String employee;
    private LocalDateTime orderDate;
    private String status;
    private String remark;

    public Order(String remark){
        this.remark = remark;
    }
}
