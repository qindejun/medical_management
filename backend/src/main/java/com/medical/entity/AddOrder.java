package com.medical.entity;

import lombok.Data;

import java.util.List;

@Data
public class AddOrder {
    private String customer;
    private String employee;
    private List<Order> items;
}
