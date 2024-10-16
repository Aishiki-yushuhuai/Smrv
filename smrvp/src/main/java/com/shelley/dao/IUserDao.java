package com.shelley.dao;

import com.shelley.entity.User;

public interface IUserDao {

    void save(User user);

    User findByUserName(String username);

}
