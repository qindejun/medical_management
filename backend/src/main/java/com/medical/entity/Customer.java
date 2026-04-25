package com.medical.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customers")
public class Customer {
    private Integer customerId;
    private String companyName;
    private String contactName;
    private String address;
    private String phone;
    private String email;
    private String industry;

}
