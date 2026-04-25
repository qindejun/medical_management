package com.medical.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import javax.persistence.Transient;


@Table(name = "employees")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private Integer EmployeeId;
    private String name;
    private String position;
    private String department;
    private String email;
    private String phone;
    private Integer role;
    private String password;
    private String gender;
    private Integer age;

    @Transient
    private String token;

    @Transient
    private String verCode;
}