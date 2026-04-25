package com.medical;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@SpringBootApplication
@MapperScan("com.medical.mapper")
public class MedicalManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicalManagementApplication.class, args);
    }

}
