package com.shelley.service;

import com.shelley.entity.User;

public interface IUserService {

    void register(User user);

    User login(User user);

}
