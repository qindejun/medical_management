package com.medical.service;

import com.medical.entity.Customer;
import com.medical.mapper.CustomerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    public List<String> getAll() {
        return customerMapper.getAll();
    }

    public void add(Customer customer) {
        customerMapper.add(customer);
    }
}
