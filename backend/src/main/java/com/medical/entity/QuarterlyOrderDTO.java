package com.medical.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuarterlyOrderDTO {
    private String quarter;
    private Integer orderCount;
    private BigDecimal totalAmount;
}
