package com.shelley.service.impl;

import com.shelley.dao.IEmployeeDao;
import com.shelley.entity.Employee;
import com.shelley.service.IEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IEmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private IEmployeeDao iEmployeeDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Employee> findAll() {
        List<Employee> all = null;
        if (redisTemplate.hasKey("employee")){
            all = (List<Employee>) redisTemplate.opsForHash().get("employee","emps");
        }else {
            all = iEmployeeDao.findAll();
            redisTemplate.opsForHash().put("employee","emps",all);
        }
        return all;
    }

    @Override
    public void save(Employee employee) {
        iEmployeeDao.save(employee);
    }

    @Override
    public void delete(Integer id) {
        iEmployeeDao.delete(id);
    }

    @Override
    public Employee findOne(Integer id) {
        return iEmployeeDao.findOne(id);
    }

    @Override
    public void update(Employee employee) {
        iEmployeeDao.update(employee);
    }
}
