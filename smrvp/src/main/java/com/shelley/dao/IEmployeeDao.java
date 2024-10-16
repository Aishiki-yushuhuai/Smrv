package com.shelley.dao;

import com.shelley.entity.Employee;

import java.util.List;

public interface IEmployeeDao {

    List<Employee> findAll();

    void save(Employee employee);

    void delete(Integer id);

    Employee findOne(Integer id);

    void update(Employee employee);

}
