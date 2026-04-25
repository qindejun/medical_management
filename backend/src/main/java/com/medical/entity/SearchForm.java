package com.medical.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchForm {
    private List<String> operationTypes;
    private List<String> timeRange;
    private Integer pageNum;
    private Integer pageSize;
    private String customer;
    private String name;
    private List<String> dateRange;
}
