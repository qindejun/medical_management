package com.medical.controller;

import com.medical.entity.Customer;
import com.medical.entity.Result;
import com.medical.service.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/customer")
@CrossOrigin
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping
    public Result getAllCustomers(){
        List<String> list = customerService.getAll();
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Customer customer){
        customerService.add(customer);
        return Result.success(customer.getCompanyName());
    }

}
