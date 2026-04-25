package com.medical.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "medicaldevices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deviceID;
    private String deviceName;
    private String modelNumber;
    private String category;
    private String manufacturer;
    private String registrationCert;
    private BigDecimal unitPrice;
    private BigDecimal costPrice;
    private String storageRequirements;
}
