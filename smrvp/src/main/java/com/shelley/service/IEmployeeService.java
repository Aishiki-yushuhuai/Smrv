package com.shelley.service;

import com.shelley.entity.Employee;

import java.util.List;

public interface IEmployeeService {

    List<Employee> findAll();

    void save(Employee employee);

    void delete(Integer id);

    Employee findOne(Integer id);

    void update(Employee employee);
}