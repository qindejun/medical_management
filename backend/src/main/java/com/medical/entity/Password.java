package com.medical.entity;

import lombok.Data;

@Data
public class Password {
    private Integer employeeId;
    private String oldPassword;
    private String newPassword;
}
